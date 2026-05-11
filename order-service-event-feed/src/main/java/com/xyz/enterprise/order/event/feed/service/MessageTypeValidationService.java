package com.xyz.enterprise.offer.event.feed.service;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.xyz.enterprise.offer.event.feed.model.constants.EventFeedConstants;

@Service
public class MessageTypeValidationService {

  private static final Logger LOGGER = LoggerFactory.getLogger(MessageTypeValidationService.class);
  private static final String INVALID_MESSAGE_TYPE = "Can not create EdaProxyRequest for messageType: {}. ";

  private static final List<String> SUPPORTED_MESSAGE_TYPES = new LinkedList<>(Arrays.asList(
      EventFeedConstants.CREATE_SOA_COMPOSITE,
      EventFeedConstants.UPDATE_SOA_COMPOSITE,
      EventFeedConstants.DELETE_SOA_COMPOSITE,
      EventFeedConstants.CREATE_EUS_COMPOSITE,
      EventFeedConstants.UPDATE_EUS_COMPOSITE,
      EventFeedConstants.DELETE_EUS_COMPOSITE,
      EventFeedConstants.CREATE_RLP_COMPOSITE,
      EventFeedConstants.UPDATE_RLP_COMPOSITE,
      EventFeedConstants.DELETE_RLP_COMPOSITE,
      EventFeedConstants.CREATE_ASRD_COMPOSITE,
      EventFeedConstants.UPDATE_ASRD_COMPOSITE,
      EventFeedConstants.DELETE_ASRD_COMPOSITE,
      EventFeedConstants.CREATE_SOA_MASTER,
      EventFeedConstants.UPDATE_SOA_MASTER,
      EventFeedConstants.DELETE_SOA_MASTER,
      EventFeedConstants.CREATE_EUS_MASTER,
      EventFeedConstants.UPDATE_EUS_MASTER,
      EventFeedConstants.DELETE_EUS_MASTER,
      EventFeedConstants.CREATE_RLP_MASTER,
      EventFeedConstants.UPDATE_RLP_MASTER,
      EventFeedConstants.DELETE_RLP_MASTER,
      EventFeedConstants.CREATE_ASRD_MASTER,
      EventFeedConstants.UPDATE_ASRD_MASTER,
      EventFeedConstants.DELETE_ASRD_MASTER
  ));

  /**
   * Checks if the provided message type is valid for processing.
   * <p>
   * This method verifies whether the given message type is supported
   * and can be safely sent or processed by the EDA system.
   * </p>
   *
   * @param messageType the type of the message to validate
   * @return {@code true} if the message type is valid; {@code false} otherwise
   */
  public boolean validMessageType(String messageType) {
    if (StringUtils.isBlank(messageType) || !SUPPORTED_MESSAGE_TYPES.contains(messageType)) {
      LOGGER.error(INVALID_MESSAGE_TYPE, messageType);
      return false;
    }
    return true;
  }
}
