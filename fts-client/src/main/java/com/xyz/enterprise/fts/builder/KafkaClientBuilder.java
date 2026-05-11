package com.xyz.enterprise.fts.builder;

import java.util.Properties;

import org.apache.kafka.clients.CommonClientConfigs;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.common.config.SaslConfigs;
import org.apache.kafka.common.serialization.StringSerializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class KafkaClientBuilder {

  private static final Logger logger = LoggerFactory.getLogger(KafkaClientBuilder.class);

  private KafkaClientBuilder() {}

  /**
   * Builds a Kafka producer with the given properties.
   *
   * @param properties the properties to configure the producer
   * @return a Kafka producer
   */
  public static KafkaProducer<String, String> buildKafkaProducer(Properties properties) {
    logger.debug("Kafka producer properties: {}", properties);
    assertNotNull(properties.getProperty(CommonClientConfigs.BOOTSTRAP_SERVERS_CONFIG),
        "bootstrap.servers cannot be null");
    assertNotNull(properties.getProperty(CommonClientConfigs.CLIENT_ID_CONFIG), "client.id cannot be null");
    assertNotNull(properties.getProperty(SaslConfigs.SASL_MECHANISM), "sasl.mechanism cannot be null");
    return new KafkaProducer<>(properties, new StringSerializer(), new StringSerializer());
  }

  private static void assertNotNull(Object s, String details) {
    if (s == null) {
      throw new IllegalArgumentException(details);
    }
  }
}
