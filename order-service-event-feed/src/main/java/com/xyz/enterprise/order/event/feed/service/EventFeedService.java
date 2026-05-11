package com.xyz.enterprise.offer.event.feed.service;

import java.util.Objects;

import javax.ws.rs.core.MultivaluedMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.xyz.enterprise.offer.event.feed.kafka.client.EdaNativeClient;
import com.xyz.enterprise.offer.event.feed.kafka.proxy.builder.EdaProxyRequestBuilder;
import com.xyz.enterprise.offer.event.feed.model.constants.Constants;
import com.xyz.enterprise.offer.event.feed.model.eda.EdaProxyRequest;

/**
 * Service for publishing messages to the EDA event feed.
 * <p>
 * Provides methods to send messages to Kafka topics and handle any necessary transformations or logging for event
 * processing.
 * </p>
 */
@Service
public class EventFeedService {

  private static final Logger LOGGER = LoggerFactory.getLogger(EventFeedService.class);
  private static final String LOG_MESSAGE_WITH_TYPE = "Processing message {} with messageType {}";
  private static final String xyz_VERSION = "3.0.xyz";

  private final EdaNativeClient edaofferProfilesProducerClient;
  private final MessageTypeValidationService messageTypeValidationService;

  public EventFeedService(EdaNativeClient edaofferProfilesProducerClient,
      MessageTypeValidationService messageTypeValidationService) {
    this.edaofferProfilesProducerClient = edaofferProfilesProducerClient;
    this.messageTypeValidationService = messageTypeValidationService;
  }

  /**
   * Sends a offer profile synchronization request to the EDA system.
   * <p>
   * This method publishes a message containing offer profile data along with associated metadata, enabling downstream
   * systems to update or synchronize offer information.
   * </p>
   *
   * @param messageMetadata metadata headers associated with the message
   * @param messageAsPlainString the message payload as a plain string
   */
  public void synchronizeofferProfile(final MultivaluedMap<String, String> messageMetadata,
      final String messageAsPlainString) {
    final String messageType = messageMetadata.getFirst(Constants.MESSAGE_TYPE);
    LOGGER.debug(LOG_MESSAGE_WITH_TYPE, messageAsPlainString, messageType);
    if (messageTypeValidationService.validMessageType(messageType) && Objects.nonNull(messageAsPlainString)) {
      messageMetadata.putSingle(Constants.xyz_API_VERSION, xyz_VERSION);
      EdaProxyRequest edaProxyRequest = EdaProxyRequestBuilder.newMessage(
          messageType, messageMetadata.getFirst(Constants.KEY), messageMetadata,
          messageAsPlainString);
      if (Objects.nonNull(edaProxyRequest)) {
        edaofferProfilesProducerClient.sendMessage(edaProxyRequest);
      }
    }
  }
}
