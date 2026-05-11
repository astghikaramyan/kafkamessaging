package com.xyz.enterprise.offer.event.feed.kafka.error;

import java.util.Collections;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.xyz.enterprise.offer.event.feed.kafka.proxy.builder.EdaRequestBuilder;
import com.xyz.enterprise.kafka.eda.model.rest.Action;
import com.xyz.enterprise.kafka.eda.model.rest.Event;
import com.xyz.enterprise.kafka.eda.model.rest.Record;
import com.xyz.enterprise.kafka.eda.model.rest.error.EdaErrorMessage;
import com.xyz.enterprise.kafka.eda.model.rest.error.EdaErrorRequest;
import com.xyz.enterprise.kafka.eda.model.rest.error.ErrorClass;
import com.xyz.enterprise.kafka.eda.model.rest.error.Severity;

@Component
@Scope("prototype")
public class ErrorRequestBuilder extends EdaRequestBuilder<EdaErrorRequest> {

  static final int ERROR_MSG_MAX_LENGTH = 5000;
  private static final int ERROR_DESC_MAX_LENGTH = 50;
  static final int DEFAULT_BUSINESS_LOGIC_ERROR_CODE = 4300;
  static final String SOURCE = "event-synchronize_offer_profiles-producer";

  @Override
  public EdaErrorRequest build() {
    EdaErrorRequest edaErrorRequest = initEdaErrorRequest();
    EdaErrorMessage errorMessage = edaErrorRequest.getRecords().get(0).getValue();
    errorMessage.setAction(Action.PRODUCE);
    event(errorMessage);
    errorMessage.setRetryCount(1);
    errorMessage.setErrorCode(DEFAULT_BUSINESS_LOGIC_ERROR_CODE);
    errorMessage.setDescription(cutOffByMaxLength(exception.getClass().getSimpleName(), ERROR_DESC_MAX_LENGTH));
    errorMessage.setErrorClass(ErrorClass.APPLICATION);
    errorMessage.setErrorMessage(
        cutOffByMaxLength(ExceptionUtils.getStackTrace(exception), ERROR_MSG_MAX_LENGTH));
    errorMessage.setSeverity(Severity.ERROR);
    return edaErrorRequest;
  }

  private void event(EdaErrorMessage errorMessage) {
    Event event = new Event();
    event.setTopicName(topicName);
    if (edaMessage != null) {
      event.setGuid(edaMessage.getMetadata().getGuid());
      event.setMessage(edaMessage.getMessage());
    }
    errorMessage.setEvent(event);
  }

  private EdaErrorRequest initEdaErrorRequest() {
    EdaErrorRequest edaErrorRequest = new EdaErrorRequest();
    Record<EdaErrorMessage> record = new Record<>();
    record.setValue(prepareErrorMessage());
    edaErrorRequest.setRecords(Collections.singletonList(record));
    return edaErrorRequest;
  }

  private EdaErrorMessage prepareErrorMessage() {
    EdaErrorMessage errorMessage = new EdaErrorMessage();
    errorMessage.setSource(SOURCE);
    errorMessage.setTimestamp(getTimestamp());
    return errorMessage;
  }
}
