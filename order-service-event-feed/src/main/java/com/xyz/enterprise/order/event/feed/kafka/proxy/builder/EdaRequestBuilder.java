package com.xyz.enterprise.offer.event.feed.kafka.proxy.builder;

import java.time.ZoneId;
import java.time.ZonedDateTime;

import com.xyz.enterprise.kafka.eda.model.EdaMessage;
import com.xyz.enterprise.kafka.eda.model.rest.EdaRestRequest;

public abstract class EdaRequestBuilder<T extends EdaRestRequest> {

  protected Exception exception = null;
  protected EdaMessage edaMessage = null;
  protected String topicName = null;

  public EdaRequestBuilder<T> edaMessage(EdaMessage edaMessage) {
    this.edaMessage = edaMessage;
    return this;
  }

  public EdaRequestBuilder<T> error(Exception e) {
    this.exception = e;
    return this;
  }

  public EdaRequestBuilder<T> topic(String ingestionTopic) {
    this.topicName = ingestionTopic;
    return this;
  }

  public abstract T build();

  protected String getTimestamp() {
    return java.time.format.DateTimeFormatter.ISO_OFFSET_DATE_TIME.format(
        ZonedDateTime.now(ZoneId.of(EdaProxyRequestBuilder.TIMESTAMP_TIMEZONE)));
  }

  protected String cutOffByMaxLength(String source, int maxLength) {
    return source != null && source.length() > maxLength ? source.substring(0, maxLength) : source;
  }
}