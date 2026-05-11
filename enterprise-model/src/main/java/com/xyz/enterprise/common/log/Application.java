package com.xyz.enterprise.common.log;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Application implements Serializable {

  @JsonProperty("id")
  private String id;
  @JsonProperty("name")
  private String name;
  @JsonProperty("environment")
  private String environment;

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getEnvironment() {
    return environment;
  }

  public void setEnvironment(String environment) {
    this.environment = environment;
  }

  @Override
  public String toString() {

    return "{"
        + "id: " + toIndentedString(id) + ","
        + " name: " + toIndentedString(name) + ","
        + " environment: " + toIndentedString(environment)
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
