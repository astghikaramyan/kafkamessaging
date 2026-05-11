package com.xyz.enterprise.common.log;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonProperty;

public class User implements Serializable {

  @JsonProperty("id")
  private String id;
  @JsonProperty("role")
  private Permission role;

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public Permission getRole() {
    return role;
  }

  public void setRole(Permission role) {
    this.role = role;
  }

  @Override
  public String toString() {
    return "{"
        + "id: " + toIndentedString(id) + ","
        + " role: " + toIndentedString(role)
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
