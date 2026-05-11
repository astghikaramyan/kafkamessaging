package com.xyz.enterprise.kafka.eda.model.rest.error;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

import org.apache.commons.lang3.builder.ToStringBuilder;

import com.xyz.enterprise.kafka.eda.model.EdaMessage;
import com.xyz.enterprise.kafka.eda.model.rest.Action;
import com.xyz.enterprise.kafka.eda.model.rest.Event;

public class EdaErrorMessage
{
  @Max(9999)
  @Min(1000)
  @NotNull
  private Integer errorCode;

  @NotNull
  private String description;

  @NotNull
  private ErrorClass errorClass;

  private String errorMessage;

  @NotNull
  private String timestamp;

  @NotNull
  private Severity severity;

  @NotNull
  private String source;

  @NotNull
  private Action action;

  @NotNull
  private Integer retryCount;

  private Event event;

  private EdaMessage EdaMessage;

  private boolean retryAfterFailure;

  public Integer getErrorCode()
  {
    return errorCode;
  }

  public void setErrorCode( Integer errorCode )
  {
    this.errorCode = errorCode;
  }

  public String getDescription()
  {
    return description;
  }

  public void setDescription( String description )
  {
    this.description = description;
  }

  public ErrorClass getErrorClass()
  {
    return errorClass;
  }

  public void setErrorClass( ErrorClass errorClass )
  {
    this.errorClass = errorClass;
  }

  public String getErrorMessage()
  {
    return errorMessage;
  }

  public void setErrorMessage( String errorMessage )
  {
    this.errorMessage = errorMessage;
  }

  public String getTimestamp()
  {
    return timestamp;
  }

  public void setTimestamp( String timestamp )
  {
    this.timestamp = timestamp;
  }

  public Severity getSeverity()
  {
    return severity;
  }

  public void setSeverity( Severity severity )
  {
    this.severity = severity;
  }

  public String getSource()
  {
    return source;
  }

  public void setSource( String source )
  {
    this.source = source;
  }

  public Action getAction()
  {
    return action;
  }

  public void setAction( Action action )
  {
    this.action = action;
  }

  public Integer getRetryCount()
  {
    return retryCount;
  }

  public void setRetryCount( Integer retryCount )
  {
    this.retryCount = retryCount;
  }

  public Event getEvent()
  {
    return event;
  }

  public void setEvent( Event event )
  {
    this.event = event;
  }

  public EdaMessage getEdaMessage() {
    return EdaMessage;
  }

  public void setEdaMessage(EdaMessage edaMessage) {
    EdaMessage = edaMessage;
  }

  public boolean isRetryAfterFailure() {
    return retryAfterFailure;
  }

  public void setRetryAfterFailure(boolean retryAfterFailure) {
    this.retryAfterFailure = retryAfterFailure;
  }

  @Override
  public String toString()
  {
    return ToStringBuilder.reflectionToString(this);
  }
}
