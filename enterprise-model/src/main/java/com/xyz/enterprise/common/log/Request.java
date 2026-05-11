package com.xyz.enterprise.common.log;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Request implements Serializable {

  @JsonProperty("id")
  private String id;
  @JsonProperty("url")
  private String url;
  @JsonProperty("method")
  private String method;

  @JsonProperty("payLoad")
  private String payLoad;
  @JsonProperty("status")
  private String status;

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public String getUrl() {
    return url;
  }

  public void setUrl(String url) {
    this.url = url;
  }

  public String getMethod() {
    return method;
  }

  public void setMethod(String method) {
    this.method = method;
  }

  public String getStatus() {
    return status;
  }

  public void setStatus(String status) {
    this.status = status;
  }

  public String getPayLoad() {
    return payLoad;
  }

  public void setPayLoad(String payLoad) {
    this.payLoad = payLoad;
  }

  @Override
  public String toString() {
    return "{"
        + "id: " + toIndentedString(id) + ","
        + " url: " + toIndentedString(url) + ","
        + " method: " + toIndentedString(method) + ","
        + " payLoad: " + toIndentedString(payLoad) + ","
        + " status: " + toIndentedString(status)
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
