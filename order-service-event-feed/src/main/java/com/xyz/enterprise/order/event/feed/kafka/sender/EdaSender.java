package com.xyz.enterprise.offer.event.feed.kafka.sender;

import java.util.Optional;
import java.util.concurrent.Future;

import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.xyz.enterprise.fts.producer.ProducerConfig;

public class EdaSender implements EventSender<String, String> {

  private static final Logger LOGGER = LoggerFactory.getLogger(EdaSender.class);

  private final ProducerConfig producerConfig;

  public EdaSender(final ProducerConfig producerConfig) {
    this.producerConfig = producerConfig;
  }

  @Override
  public Future<RecordMetadata> send(String topic, String key, String value) {
    LOGGER.debug("Sending event to EDA (topic: {}, key: {}, value: {})", topic, key, value);
    return producerConfig.getProducer().send(
        new ProducerRecord<>(topic, key, value),
        senderCallback(
            topic,
            getBytesOrNull(key),
            value.getBytes()
        )
    );
  }

  @Override
  public Future<RecordMetadata> send(String topic, String value) {
    LOGGER.debug("Sending event to EDA (topic: {}, value: {})", topic, value);
    return producerConfig.getProducer().send(
        new ProducerRecord<>(topic, value),
        senderCallback(
            topic,
            null,
            value.getBytes()
        )
    );
  }

  private byte[] getBytesOrNull(final String key) {
    return Optional.ofNullable(key).map(String::getBytes).orElse(null);
  }
}
