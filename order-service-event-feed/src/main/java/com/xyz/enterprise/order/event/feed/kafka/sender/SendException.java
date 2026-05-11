package com.xyz.enterprise.offer.event.feed.kafka.sender;

public class SendException extends RuntimeException {

  private static final long serialVersionUID = 1L;

  public SendException(Throwable cause) {
    super(cause);
  }
}
