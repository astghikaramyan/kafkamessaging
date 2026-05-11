package com.xyz.enterprise.offer.event.feed.kafka.error;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.ObjectFactory;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Component;

import com.xyz.enterprise.offer.event.feed.kafka.sender.EdaEventProducer;
import com.xyz.enterprise.kafka.eda.model.EdaMessage;
import com.xyz.enterprise.kafka.eda.model.rest.error.EdaErrorRequest;

/**
 * Service for logging errors to the EDA error topic.
 * <p>
 * Responsible for sending error details to the designated Kafka topic
 * for monitoring and troubleshooting purposes.
 * </p>
 */
@Component
public class EdaErrorLogger {

  private static final Logger LOGGER = LoggerFactory.getLogger(EdaErrorLogger.class);
  private static final String LOG_PROCESS_FAILURE_MESSAGE_FOR_TOPIC = "Processing failures message for topic: {}";

  private final ObjectFactory<ErrorRequestBuilder> errorRequestBuilder;
  private final ThreadPoolTaskExecutor asyncEdaErrorExecutor;
  private final EdaEventProducer edaEventProducer;

  public EdaErrorLogger(
      ObjectFactory<ErrorRequestBuilder> errorRequestBuilder,
      ThreadPoolTaskExecutor asyncEdaErrorExecutor,
      EdaEventProducer edaEventProducer
  ) {
    this.errorRequestBuilder = errorRequestBuilder;
    this.asyncEdaErrorExecutor = asyncEdaErrorExecutor;
    this.edaEventProducer = edaEventProducer;
  }

  /**
   * Logs errors with {@code errorClass == Application} (errors that occurred in the application, for example, during
   * message parsing or business logic execution).
   *
   * @param edaMessage the message on which the error occurred
   * @param e the exception that was thrown
   */
  public void logApplicationError(EdaMessage edaMessage, Exception e, String ingestionTopic) {
    asyncEdaErrorExecutor.submit(() -> {
      EdaErrorRequest edaErrorRequest = errorRequestBuilder.getObject()
          .edaMessage(edaMessage)
          .error(e)
          .topic(ingestionTopic)
          .build();
      LOGGER.debug(LOG_PROCESS_FAILURE_MESSAGE_FOR_TOPIC, ingestionTopic);
      edaEventProducer.send(edaErrorRequest, ingestionTopic);
    });
  }
}
