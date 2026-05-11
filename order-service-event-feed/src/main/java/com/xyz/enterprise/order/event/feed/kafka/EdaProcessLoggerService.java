package com.xyz.enterprise.offer.event.feed.kafka;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.xyz.enterprise.offer.event.feed.kafka.audit.EdaAuditLogger;
import com.xyz.enterprise.offer.event.feed.kafka.error.EdaErrorLogger;
import com.xyz.enterprise.kafka.eda.model.EdaMessage;

@Service
public class EdaProcessLoggerService {

  private final EdaAuditLogger edaAuditLogger;
  private final EdaErrorLogger edaErrorLogger;

  public EdaProcessLoggerService(
      @Autowired(required = false) EdaAuditLogger edaAuditLogger,
      @Autowired(required = false) EdaErrorLogger edaErrorLogger
  ) {
    this.edaAuditLogger = edaAuditLogger;
    this.edaErrorLogger = edaErrorLogger;
  }

  public void logSuccess(EdaMessage edaMessage, String ingestionTopic) {
    edaAuditLogger.logSuccess(edaMessage, ingestionTopic);
  }

  public void logFailure(EdaMessage edaMessage, Exception e, String ingestionTopic) {
    edaErrorLogger.logApplicationError(edaMessage, e, ingestionTopic);
  }
}
