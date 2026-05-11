package com.xyz.enterprise.offer.event.feed.exceptions;

import lombok.Data;

/**
 * Exception occurred during connection to any other service using HTTP, e.g. EDA Rest Proxy, etc
 */
@Data
public class RemoteConnectionException extends RuntimeException {

  private final int statusCode;

  private final String message;

  public RemoteConnectionException(int statusCode, String message) {
    this.statusCode = statusCode;
    this.message = message;
  }
}