package com.xyz.enterprise.offer.event.feed.kafka.proxy.builder;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Collections;
import java.util.UUID;
import java.util.zip.GZIPOutputStream;

import javax.ws.rs.core.MultivaluedMap;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.xyz.enterprise.offer.event.feed.model.constants.Constants;
import com.xyz.enterprise.offer.event.feed.model.eda.EdaProxyRequest;
import com.xyz.enterprise.offer.event.feed.model.eda.Record;
import com.xyz.enterprise.kafka.eda.model.Context;
import com.xyz.enterprise.kafka.eda.model.EdaMessage;
import com.xyz.enterprise.kafka.eda.model.Metadata;

/**
 * Builder class for creating {@link EdaProxyRequest} instances.
 * <p>
 * Provides convenient methods to construct EDA proxy requests with
 * necessary payload, headers, and metadata for sending to EDA topics.
 * </p>
 * <p>
 * Example usage:
 * <pre>
 *     EdaProxyRequest request = new EdaProxyRequestBuilder()
 *                                   .message("example message")
 *                                   .build();
 * </pre>
 * </p>
 */
public class EdaProxyRequestBuilder {

  private static final Logger LOGGER = LoggerFactory.getLogger(EdaProxyRequestBuilder.class);
  private static final String EDA_API_VERSION = "1.0";
  private static final String EDA_REQUEST_SOURCE = "AHP";
  private static final String KAFKA_KEY_FOR_CORPORATE_FEEDS = "ZCORP";
  private static final String MASTER_ID = "masterId";
  private static final String GZIP_BASE_64 = "gzip-base64";
  public static final String TIMESTAMP_TIMEZONE = "GMT";
  private EdaProxyRequest message;
  private Record record;

  EdaProxyRequestBuilder() {
    this.message = new EdaProxyRequest();
    this.record = new Record();
    Metadata metadata = new Metadata();
    EdaMessage edaMessage = new EdaMessage();
    edaMessage.setMetadata(metadata);
    edaMessage.setContext(new ArrayList<>());
    record.setValue(edaMessage);
  }

  /**
   * Creates a new {@link EdaProxyRequest} with the specified details.
   * <p>
   * Initializes a fresh EDA proxy request with the given operation name, key,
   * metadata, and message payload.
   * </p>
   *
   * @param operationName the name of the operation associated with this request
   * @param key the unique key for the message
   * @param messageMetadata metadata headers for the message
   * @param messageAsPlainString the message payload as a plain string
   * @return a new {@link EdaProxyRequest} instance initialized with the provided values
   */
  public static EdaProxyRequest newMessage(
      String operationName, String key, MultivaluedMap<String, String> messageMetadata, String messageAsPlainString) {
    try {
      return new EdaProxyRequestBuilder()
          .key(StringUtils.isEmpty(key) ? KAFKA_KEY_FOR_CORPORATE_FEEDS : key)
          .id(UUID.randomUUID().toString())
          .version(messageMetadata)
          .correlationId(messageMetadata.getFirst(Constants.AMA_CORRELATION_ID))
          .type(operationName)
          .source(EDA_REQUEST_SOURCE)
          .generatedTime(DateTimeFormatter.ISO_OFFSET_DATE_TIME.format(
              ZonedDateTime.now(ZoneId.of(TIMESTAMP_TIMEZONE))))
          .transmissionTime(DateTimeFormatter.ISO_OFFSET_DATE_TIME.format(
              ZonedDateTime.now(ZoneId.of(TIMESTAMP_TIMEZONE))))
          .retransmitCount(
              messageMetadata.containsKey(Constants.AMA_RETRY_COUNT)
                  ? Integer.valueOf(messageMetadata.getFirst(Constants.AMA_RETRY_COUNT))
                  : Integer.valueOf(Constants.AMA_RETRY_DEFAULT_COUNT))
          .context(
              MASTER_ID, StringUtils.isEmpty(key) ? KAFKA_KEY_FOR_CORPORATE_FEEDS : key)
          .context(
              Constants.AMA_REQUEST_ID.toLowerCase(),
              messageMetadata.containsKey(Constants.AMA_REQUEST_ID)
                  ? messageMetadata.getFirst(Constants.AMA_REQUEST_ID)
                  : String.valueOf(UUID.randomUUID()))
          .context(
              messageMetadata.containsKey(Constants.xyz_API_VERSION) ? Constants.xyz_API_VERSION.toLowerCase() : null,
              messageMetadata.getFirst(Constants.xyz_API_VERSION))
          .message(messageAsPlainString)
          .encoding(GZIP_BASE_64)
          .build();
    } catch (Exception e) {
      LOGGER.error("Can not build message from provided request", e);
      return null;
    }
  }

  /**
   * Sets the key for the {@link EdaProxyRequest} being built.
   * <p>
   * The key is typically used for message partitioning in Kafka.
   * </p>
   *
   * @param key the key to associate with the message
   * @return the current {@link EdaProxyRequestBuilder} instance for method chaining
   */
  public EdaProxyRequestBuilder key(String key) {
    this.record.setKey(key);
    return this;
  }

  /**
   * Adds a context key-value pair to the {@link EdaProxyRequest} being built.
   * <p>
   * Context entries can be used to attach additional metadata or properties
   * to the message for downstream processing.
   * </p>
   *
   * @param key the context key
   * @param value the value associated with the key
   * @return the current {@link EdaProxyRequestBuilder} instance for method chaining
   */
  public EdaProxyRequestBuilder context(String key, String value) {
    if (StringUtils.isEmpty(key)) {
      return this;
    }
    Context context = new Context();
    context.setKey(key);
    context.setValue(value);
    this.record.getValue().getContext().add(context);
    return this;
  }

  /**
   * Sets the unique identifier for the {@link EdaProxyRequest} being built.
   * <p>
   * The ID can be used to track or correlate messages across systems.
   * </p>
   *
   * @param id the unique identifier for the message
   * @return the current {@link EdaProxyRequestBuilder} instance for method chaining
   */
  public EdaProxyRequestBuilder id(String id) {
    this.record.getValue().getMetadata().setGuid(id);
    return this;
  }

  /**
   * Sets version-related metadata for the {@link EdaProxyRequest} being built.
   * <p>
   * This metadata can include version information or other properties
   * required for downstream processing of the message.
   * </p>
   *
   * @param metadata the version or related metadata to attach to the message
   * @return the current {@link EdaProxyRequestBuilder} instance for method chaining
   */
  public EdaProxyRequestBuilder version(MultivaluedMap<String, String> metadata) {
    String versionData = EDA_API_VERSION;
    if (metadata.containsKey(Constants.xyz_API_VERSION)) {
      versionData = metadata.getFirst(Constants.xyz_API_VERSION);
    } else if (metadata.containsKey(Constants.AMA_API_VERSION)) {
      versionData = metadata.getFirst(Constants.AMA_API_VERSION);
    }
    this.record.getValue().getMetadata().setVersion(versionData);
    return this;
  }

  /**
   * Sets the correlation ID for the {@link EdaProxyRequest} being built.
   * <p>
   * The correlation ID is used to track or link related messages across
   * different systems or processing stages.
   * </p>
   *
   * @param correlationId the correlation ID to associate with the message
   * @return the current {@link EdaProxyRequestBuilder} instance for method chaining
   */
  public EdaProxyRequestBuilder correlationId(String correlationId) {
    this.record.getValue().getMetadata().setCorrelationId(correlationId);
    return this;
  }

  /**
   * Sets the type of the {@link EdaProxyRequest} being built.
   * <p>
   * The type indicates the category or purpose of the message, which can
   * be used by downstream systems to handle the message appropriately.
   * </p>
   *
   * @param type the type to associate with the message
   * @return the current {@link EdaProxyRequestBuilder} instance for method chaining
   */
  public EdaProxyRequestBuilder type(String type) {
    this.record.getValue().getMetadata().setType(type);
    return this;
  }

  /**
   * Sets the source of the {@link EdaProxyRequest} being built.
   * <p>
   * The source identifies the origin of the message, which can be useful
   * for tracking, auditing, or routing purposes in downstream systems.
   * </p>
   *
   * @param source the source to associate with the message
   * @return the current {@link EdaProxyRequestBuilder} instance for method chaining
   */
  public EdaProxyRequestBuilder source(String source) {
    this.record.getValue().getMetadata().setSource(source);
    return this;
  }

  /**
   * Sets the retransmit count for the {@link EdaProxyRequest} being built.
   * <p>
   * This value indicates how many times the message has been retransmitted,
   * which can be used for monitoring, logging, or retry logic in downstream processing.
   * </p>
   *
   * @param retransmitCount the number of times the message has been retransmitted
   * @return the current {@link EdaProxyRequestBuilder} instance for method chaining
   */
  public EdaProxyRequestBuilder retransmitCount(Integer retransmitCount) {
    this.record.getValue().getMetadata().setRetransmitCount(retransmitCount);
    return this;
  }

  /**
   * Sets the generated time for the {@link EdaProxyRequest} being built.
   * <p>
   * This timestamp indicates when the message was created, which can be used
   * for auditing, ordering, or timeout handling in downstream systems.
   * </p>
   *
   * @param generatedTime the timestamp string representing when the message was generated
   * @return the current {@link EdaProxyRequestBuilder} instance for method chaining
   */
  public EdaProxyRequestBuilder generatedTime(String generatedTime) {
    this.record.getValue().getMetadata().setGeneratedTime(generatedTime);
    return this;
  }

  /**
   * Sets the transmission time for the {@link EdaProxyRequest} being built.
   * <p>
   * This timestamp indicates when the message is sent, which can be used for
   * auditing, ordering, or latency tracking in downstream systems.
   * </p>
   *
   * @param transmissionTime the timestamp string representing when the message is transmitted
   * @return the current {@link EdaProxyRequestBuilder} instance for method chaining
   */
  public EdaProxyRequestBuilder transmissionTime(String transmissionTime) {
    this.record.getValue().getMetadata().setTransmissionTime(transmissionTime);
    return this;
  }

  /**
   * Sets the payload of the {@link EdaProxyRequest} being built.
   * <p>
   * The payload represents the actual message content to be sent to the EDA topic.
   * </p>
   *
   * @param messageAsPlainString the message content as a plain string
   * @return the current {@link EdaProxyRequestBuilder} instance for method chaining
   */
  public EdaProxyRequestBuilder message(String messageAsPlainString)
      throws IOException {
    LOGGER.debug("Base 64 encoded messageAsPlainString {} ", messageAsPlainString);
    ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream(messageAsPlainString.length() / 10);
    GZIPOutputStream gzip = new GZIPOutputStream(byteArrayOutputStream);
    gzip.write(messageAsPlainString.getBytes(StandardCharsets.UTF_8));
    gzip.close();
    byte[] encodedMessage = Base64.getEncoder().encode(byteArrayOutputStream.toByteArray());
    this.record.getValue().setMessage(new String(encodedMessage));
    return this;
  }

  /**
   * Sets the encoding for the {@link EdaProxyRequest} being built.
   * <p>
   * The encoding specifies the character set used for the message payload,
   * which can be important for correct serialization and downstream processing.
   * </p>
   *
   * @param encoding the character encoding of the message payload
   * @return the current {@link EdaProxyRequestBuilder} instance for method chaining
   */
  public EdaProxyRequestBuilder encoding(String encoding) {
    this.record.getValue().getMetadata().setContentEncoding(encoding);
    return this;
  }

  /**
   * Builds and returns the {@link EdaProxyRequest} instance.
   * <p>
   * This method finalizes all the properties set via the builder methods
   * and creates a fully initialized {@link EdaProxyRequest} ready for sending.
   * </p>
   *
   * @return a fully constructed {@link EdaProxyRequest} instance
   */
  public EdaProxyRequest build() {
    message.setRecords(Collections.singletonList(record));
    return message;
  }
}
