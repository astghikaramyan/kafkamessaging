package com.xyz.enterprise.offer.event.feed.kafka.audit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.ObjectFactory;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Component;

import com.xyz.enterprise.offer.event.feed.kafka.sender.EdaEventProducer;
import com.xyz.enterprise.kafka.eda.model.EdaMessage;
import com.xyz.enterprise.kafka.eda.model.rest.audit.EdaAuditRequest;

/**
 * <p>
 * EdaAuditLogger is responsible for logging audit events for messages processed within the EDA (Enterprise Data Access)
 * system. It captures key metadata, message payloads, and any relevant headers for audit and compliance purposes.
 * </p>
 */
@Component
public class EdaAuditLogger {

  private static final Logger LOGGER = LoggerFactory.getLogger(EdaAuditLogger.class);
  private static final String LOG_PROCESS_AUDIT_MESSAGE_FOR_TOPIC = "Processing audit message for topic: {}";

  private final ObjectFactory<AuditRequestBuilder> auditRequestBuilder;
  private final ThreadPoolTaskExecutor asyncEdaAuditExecutor;
  private final EdaEventProducer edaEventProducer;

  public EdaAuditLogger(
      ObjectFactory<AuditRequestBuilder> auditRequestBuilder,
      ThreadPoolTaskExecutor asyncEdaAuditExecutor,
      EdaEventProducer edaEventProducer
  ) {
    this.auditRequestBuilder = auditRequestBuilder;
    this.asyncEdaAuditExecutor = asyncEdaAuditExecutor;
    this.edaEventProducer = edaEventProducer;
  }

  /**
   * Logs a successfully processed EDA message.
   *
   * @param edaMessage the {@link EdaMessage} that was successfully processed
   * @param ingestionTopic the name of the topic where the message was ingested
   */
  public void logSuccess(EdaMessage edaMessage, String ingestionTopic) {
    asyncEdaAuditExecutor.submit(() -> {
      EdaAuditRequest edaAuditRequest = auditRequestBuilder.getObject()
          .edaMessage(edaMessage)
          .topic(ingestionTopic)
          .build();
      LOGGER.debug(LOG_PROCESS_AUDIT_MESSAGE_FOR_TOPIC, ingestionTopic);
      edaEventProducer.send(edaAuditRequest, ingestionTopic);
    });
  }
}
