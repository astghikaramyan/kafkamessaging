package com.xyz.enterprise.offer.event.feed.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

@Configuration
public class Config {

  /**
   * ObjectMapper used by EdaNativeClient and EdaEventProducer to serialize messages to JSON before publishing to Kafka
   * topics. Injected by bean name "restTemplateMapper".
   */
  @Bean
  public ObjectMapper restTemplateMapper() {
    return new ObjectMapper()
        .registerModule(new JavaTimeModule())
        .setSerializationInclusion(JsonInclude.Include.NON_EMPTY)
        .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
        .configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
  }

  @Primary
  @Bean
  public ObjectMapper objectMapper() {
    return new ObjectMapper()
        .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
        .configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
  }
}
