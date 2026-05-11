package com.xyz.enterprise.offer.event.feed.kafka.client;

import java.util.Objects;
import java.util.Optional;

import org.apache.kafka.clients.producer.RecordMetadata;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.xyz.enterprise.offer.event.feed.exceptions.RemoteConnectionException;
import com.xyz.enterprise.offer.event.feed.kafka.EdaProcessLoggerService;
import com.xyz.enterprise.offer.event.feed.kafka.sender.EventSender;
import com.xyz.enterprise.offer.event.feed.model.eda.EdaProxyRequest;
import com.xyz.enterprise.offer.event.feed.model.eda.Record;
import com.xyz.enterprise.kafka.eda.model.EdaMessage;
import com.xyz.enterprise.kafka.eda.model.Metadata;

/**
 * Kafka client for sending {@link EdaProxyRequest}s to EDA topics.
 * <p>
 * Provides methods to publish events natively using the configured Kafka producer.
 * Handles serialization and logging of messages.
 * </p>
 */
@Component
public class EdaNativeClient {

  private static final Logger LOGGER = LoggerFactory.getLogger(EdaNativeClient.class);

  private static final int STATUS_CODE = 50003;

  private final String synchronizeofferProfileTopic;
  private final String synchronizeofferProfileAuditTopic;
  private final String synchronizeofferProfileErrorTopic;
  private final EventSender<String, String> edaSender;
  private final EdaProcessLoggerService edaProcessLoggerService;
  private final ObjectMapper restTemplateMapper;

  public EdaNativeClient(
      @Value("${eda.kafka.offer.feeds.topic}") String synchronizeofferProfileTopic,
      @Value("${eda.kafka.offer.feeds.audit.topic}") String synchronizeofferProfileAuditTopic,
      @Value("${eda.kafka.offer.feeds.error.topic}") String synchronizeofferProfileErrorTopic,
      @Autowired EventSender<String, String> edaSender,
      @Autowired(required = false) EdaProcessLoggerService edaProcessLoggerService,
      @Autowired ObjectMapper restTemplateMapper
  ) {
    this.synchronizeofferProfileTopic = synchronizeofferProfileTopic;
    this.synchronizeofferProfileAuditTopic = synchronizeofferProfileAuditTopic;
    this.synchronizeofferProfileErrorTopic = synchronizeofferProfileErrorTopic;
    this.edaSender = edaSender;
    this.edaProcessLoggerService = edaProcessLoggerService;
    this.restTemplateMapper = restTemplateMapper;
  }

  /**
   * Sends the given message to a Kafka topic.
   *
   * @param message the {@link EdaProxyRequest} instance to send
   */
  public void sendMessage(EdaProxyRequest message) {
    if (Objects.nonNull(message)) {
      EdaMessage edaMessage = null;
      try {
        LOGGER.debug("Sending message to: {} topic", synchronizeofferProfileTopic);
        final Record messageRecord = message.getRecords().get(0);
        final String edaMessageKey = messageRecord.getKey();
        edaMessage = messageRecord.getValue();
        LOGGER.debug("Sending to EDA \n\ttopic: {}\n\tbody: {}", synchronizeofferProfileTopic, message);
        String operationName = Optional.ofNullable(edaMessage)
            .map(EdaMessage::getMetadata).map(Metadata::getType).orElse(null);
        final String eventValue = objectToString(edaMessage);
        LOGGER.debug("synchronize_offer_profiles - operationName : {}", operationName);
        checkResponse(edaSender.blockingSend(synchronizeofferProfileTopic, edaMessageKey, eventValue));
        auditLogSuccess(edaMessage);
      } catch (Exception e) {
        LOGGER.error("Exception {} occurred during sending event to EDA {}", e, synchronizeofferProfileTopic);
        auditLogFailure(edaMessage, e);
      }
    }
  }

  private void auditLogFailure(EdaMessage edaMessage, Exception e) {
    edaProcessLoggerService.logFailure(edaMessage, e, synchronizeofferProfileErrorTopic);
  }

  private void auditLogSuccess(EdaMessage edaMessage) {
    edaProcessLoggerService.logSuccess(edaMessage, synchronizeofferProfileAuditTopic);
  }

  private <T> String objectToString(T obj) {
    try {
      return restTemplateMapper.writeValueAsString(obj);
    } catch (JsonProcessingException e) {
      throw new RemoteConnectionException(STATUS_CODE, "Failed to serialize object to JSON : " + e.getMessage());
    }
  }

  private void checkResponse(RecordMetadata responseEntity) {
    try {
      if (!responseEntity.hasOffset()) {
        throw new RemoteConnectionException(STATUS_CODE,
            "Send event to EDA was unsuccessful. Topic : " + synchronizeofferProfileTopic);
      }
    } catch (Exception e) {
      throw new RemoteConnectionException(STATUS_CODE,
          "Kafka exception. Although the operation failed, it's possible that retrying the request will be successful.");
    }
  }
}
