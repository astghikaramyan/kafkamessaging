package com.xyz.enterprise.common.log;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Error implements Serializable {

  @JsonProperty("code")
  private String code;
  @JsonProperty("message")
  private String message;
  @JsonProperty("stackTrace")
  private String stackTrace;

  public String getCode() {
    return code;
  }

  public void setCode(String code) {
    this.code = code;
  }

  public String getMessage() {
    return message;
  }

  public void setMessage(String message) {
    this.message = message;
  }

  public String getStackTrace() {
    return stackTrace;
  }

  public void setStackTrace(String stackTrace) {
    this.stackTrace = stackTrace;
  }

  @Override
  public String toString() {
    return "{"
        + "code: " + toIndentedString(code) + ","
        + " message: " + toIndentedString(message) + ","
        + " stackTrace: " + toIndentedString(stackTrace)
        + "}";
  }

  /**
   * Convert the given object to string with each line indented by 4 spaces
   * (except the first line).
   */
  private String toIndentedString(Object o) {
    if (o == null) {
      return "null";
    }
    return o.toString().replace("\n", "\n    ");
  }
}
