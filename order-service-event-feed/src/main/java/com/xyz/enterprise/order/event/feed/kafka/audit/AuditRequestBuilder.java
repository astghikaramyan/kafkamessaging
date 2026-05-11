package com.xyz.enterprise.offer.event.feed.kafka.audit;

import java.util.Collections;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.xyz.enterprise.offer.event.feed.kafka.proxy.builder.EdaRequestBuilder;
import com.xyz.enterprise.kafka.eda.model.Metadata;
import com.xyz.enterprise.kafka.eda.model.rest.Action;
import com.xyz.enterprise.kafka.eda.model.rest.Record;
import com.xyz.enterprise.kafka.eda.model.rest.audit.EdaAuditMessage;
import com.xyz.enterprise.kafka.eda.model.rest.audit.EdaAuditRequest;

@Component
@Scope("prototype")
public class AuditRequestBuilder extends EdaRequestBuilder<EdaAuditRequest> {

  static final String AMA_REQUEST_ID_KEY = "ama-request-id";
  static final String FAILURE_STATUS_DESC = "Failure";
  static final String SUCCESS_STATUS_MESG = "Event processed successfully";
  static final String SUCCESS_STATUS_DESC = "Success";
  static final String SUCCESS_STATUS_CODE = "200";
  static final String FAILURE_STATUS_CODE = "400";
  static final String EVENT_SOURCE_CODE = "eventchs-ahpevents-producer";

  @Override
  public EdaAuditRequest build() {
    EdaAuditRequest edaAuditRequest = new EdaAuditRequest();
    Record<EdaAuditMessage> record = new Record<>();
    edaAuditRequest.setRecords(Collections.singletonList(record));
    EdaAuditMessage edaAuditMessage = new EdaAuditMessage();
    record.setValue(edaAuditMessage);

    edaAuditMessage.setAction(Action.PRODUCE);
    edaAuditMessage.setGuid(edaMessage.getMetadata().getGuid());
    edaAuditMessage.setAuditTransmitTime(getTimestamp());
    edaAuditMessage.setAuditSource(EVENT_SOURCE_CODE);
    edaAuditMessage.setTopicName(topicName);
    edaAuditMessage.setType(edaMessage.getMetadata().getType());

    edaAuditMessage.setAmaRequestId(edaMessage.findValueInContext(AMA_REQUEST_ID_KEY));
    edaAuditMessage.setTopicName(topicName);

    captureMetadataToAudit(edaAuditMessage);

    if (exception == null) {
      populateSuccessStatus(edaAuditMessage);
    } else {
      populatedFailureStatus(edaAuditMessage);
    }
    return edaAuditRequest;
  }

  private void populatedFailureStatus(EdaAuditMessage edaAuditMessage) {
    edaAuditMessage.setStatusCode(FAILURE_STATUS_CODE);
    edaAuditMessage.setStatusDesc(FAILURE_STATUS_DESC);
    edaAuditMessage.setStatusMessage(exception.getMessage());
  }

  private void captureMetadataToAudit(EdaAuditMessage edaAuditMessage) {
    Metadata metadata = edaMessage.getMetadata();
    if (metadata != null) {
      edaAuditMessage.setGuid(metadata.getGuid());
      edaAuditMessage.setCorrelationId(metadata.getCorrelationId());
      edaAuditMessage.setSequenceId(metadata.getSequenceId());
      edaAuditMessage.setVersion(metadata.getVersion());
      edaAuditMessage.setSource(metadata.getSource());
      edaAuditMessage.setRetransmitCount(metadata.getRetransmitCount());
      edaAuditMessage.setGeneratedTime(metadata.getGeneratedTime());
      edaAuditMessage.setTransmissionTime(metadata.getTransmissionTime());
    }
  }

  private void populateSuccessStatus(EdaAuditMessage edaAuditMessage) {
    edaAuditMessage.setStatusCode(SUCCESS_STATUS_CODE);
    edaAuditMessage.setStatusDesc(SUCCESS_STATUS_DESC);
    edaAuditMessage.setStatusMessage(SUCCESS_STATUS_MESG);
  }
}
