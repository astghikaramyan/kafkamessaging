package com.xyz.enterprise.offer.event.feed.kafka.sender;

import java.util.Optional;

import org.apache.kafka.clients.producer.ProducerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.xyz.enterprise.offer.event.feed.exceptions.RemoteConnectionException;
import com.xyz.enterprise.kafka.eda.model.rest.EdaRestRequest;

@Service
public class EdaEventProducer {

  private static final int STATUS_CODE = 50003;
  private static final Logger LOGGER = LoggerFactory.getLogger(EdaEventProducer.class);

  private final EventSender<String, String> edaSender;
  private final ObjectMapper restTemplateMapper;

  public EdaEventProducer(EventSender<String, String> edaSender, ObjectMapper restTemplateMapper) {
    this.edaSender = edaSender;
    this.restTemplateMapper = restTemplateMapper;
  }

  public void send(EdaRestRequest edaRestRequest, final String topicName) {
    LOGGER.debug("Sending to EDA \n\tTopic: {}\n\tbody: {}", topicName, edaRestRequest);
    try {
      final ProducerRecord<String, String> eventRecord = buildSendEvent(edaRestRequest, topicName);
      LOGGER.debug("Sending request to EDA : {}", eventRecord);
      edaSender.send(topicName, eventRecord.key(), eventRecord.value());
      LOGGER.debug("Request to EDA {} was sent successfully", topicName);
    } catch (Exception e) {
      LOGGER.error("Exception {} occurred during sending request to EDA {}", e, topicName);
    }
  }

  /**
   * Builds a {@link ProducerRecord} from the given {@link EdaRestRequest}.
   * <p>
   * Prepares the Kafka record for sending to the appropriate EDA topic, including key, value, and any necessary headers
   * or metadata.
   * </p>
   *
   * @param edaRestRequest the {@link EdaRestRequest} to convert into a Kafka record
   * @return a {@link ProducerRecord} representing the event to be sent
   */
  public ProducerRecord<String, String> buildSendEvent(final EdaRestRequest<?> edaRestRequest, final String topicName) {
    return Optional.ofNullable(edaRestRequest)
        .map(EdaRestRequest::getRecords)
        .map(records -> records.get(0))
        .map(record -> {
          final String json = objectToString(record.getValue());
          return new ProducerRecord<>(topicName, record.getKey(), json);
        })
        .orElseThrow(() -> new RemoteConnectionException(STATUS_CODE,
            "Failed to convert edaRestRequest to ProducerRecord : " + edaRestRequest));
  }

  private <T> String objectToString(T obj) {
    try {
      return restTemplateMapper.writeValueAsString(obj);
    } catch (JsonProcessingException e) {
      throw new RemoteConnectionException(STATUS_CODE, "Failed to serialize object to JSON : " + e.getMessage());
    }
  }
}
