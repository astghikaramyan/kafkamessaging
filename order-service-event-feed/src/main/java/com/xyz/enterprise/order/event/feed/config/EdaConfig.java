package com.xyz.enterprise.offer.event.feed.config;

import java.util.Properties;
import java.util.UUID;
import java.util.concurrent.ThreadPoolExecutor;

import org.apache.kafka.clients.CommonClientConfigs;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.common.config.SaslConfigs;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.jackson.Jackson2ObjectMapperBuilderCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import com.fasterxml.jackson.databind.SerializationFeature;
import com.xyz.enterprise.offer.event.feed.kafka.sender.EdaSender;
import com.xyz.enterprise.offer.event.feed.kafka.sender.EventSender;
import com.xyz.enterprise.fts.producer.ProducerConfig;

/**
 * Spring configuration class for EDA components.
 * <p>
 * Enables component scanning in the package
 * <code>com.xyz.enterprise.fts.producer</code> to automatically
 * detect and register Spring beans.
 * </p>
 */
@Configuration
@ComponentScan(basePackages = "com.xyz.enterprise.fts.producer")
public class EdaConfig {

  @Value("${eda.audit.rest.executor.corePoolSize:2}")
  private int corePoolSize;
  @Value("${eda.audit.rest.executor.maxPoolSize:2}")
  private int maxPoolSize;
  @Value("${eda.audit.rest.executor.queue.capacity:100}")
  private int queueCapacity;
  @Value("${eda.kafka.security.protocol:SASL_SSL}")
  private String securityProtocol;
  @Value("${eda.kafka.producer.buffer.memory:33554432}")
  private Long bufferMemory;
  @Value("${eda.kafka.producer.batch.size:16384}")
  private Integer batchSize;
  @Value("${eda.kafka.producer.linger.ms:10}")
  private Integer lingerMs;
  @Value("${eda.kafka.producer.enable.idempotence:true}")
  private Boolean enableIdempotence;
  @Value("${eda.kafka.producer.acks:all}")
  private String acks;
  @Value("${eda.kafka.producer.request.timeout.ms:30000}")
  private Integer requestTimeout;
  @Value("${eda.kafka.producer.delivery.timeout.ms:120000}")
  private Integer deliveryTimeout;
  @Value("${eda.kafka.producer.compression.type:none}")
  private String compressionType;
  @Value("${eda.kafka.primary.bootstrap.servers}")
  private String primaryBootstrap;
  @Value("${eda.kafka.primary.sasl.jaas.config:}")
  private String primarySaslJaasConfig;
  @Value("${eda.kafka.dr.bootstrap.servers}")
  private String drBootstrap;
  @Value("${eda.kafka.dr.sasl.jaas.config:}")
  private String drSaslJaasConfig;
  @Value("${eda.kafka.wid}")
  private String clientWid;
  @Value("${eda.kafka.sasl.mechanism:PLAIN}")
  private String saslMechanism;

  /**
   * Configures a {@link ThreadPoolTaskExecutor} for asynchronous EDA audit processing.
   * <p>
   * This executor is used to run audit logging tasks in the background, allowing
   * non-blocking processing of messages.
   * </p>
   *
   * @return a configured {@link ThreadPoolTaskExecutor} instance
   */
  @Bean
  public ThreadPoolTaskExecutor asyncEdaAuditExecutor() {
    ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
    executor.setCorePoolSize(corePoolSize);
    executor.setMaxPoolSize(maxPoolSize);
    executor.setQueueCapacity(queueCapacity);
    executor.setRejectedExecutionHandler(new ThreadPoolExecutor.DiscardOldestPolicy());
    executor.setThreadNamePrefix("EdaAuditLogger-");
    executor.initialize();
    return executor;
  }

  /**
   * Configures a {@link ThreadPoolTaskExecutor} for asynchronous EDA error processing.
   * <p>
   * This executor handles tasks for logging and processing errors in the background,
   * ensuring that error handling does not block main message processing.
   * </p>
   *
   * @return a configured {@link ThreadPoolTaskExecutor} instance
   */
  @Bean
  public ThreadPoolTaskExecutor asyncEdaErrorExecutor() {
    ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
    executor.setCorePoolSize(corePoolSize);
    executor.setMaxPoolSize(maxPoolSize);
    executor.setQueueCapacity(queueCapacity);
    executor.setRejectedExecutionHandler(new ThreadPoolExecutor.DiscardOldestPolicy());
    executor.setThreadNamePrefix("EdaErrorLogger-");
    executor.initialize();
    return executor;
  }

  /**
   * Provides the primary {@link ProducerConfig} for Kafka message production.
   * <p>
   * This configuration bean is used to create Kafka producers for sending messages
   * to EDA topics, including necessary producer settings such as bootstrap servers,
   * key/value serializers, and other producer properties.
   * </p>
   *
   * @return a configured {@link ProducerConfig} instance
   */
  @Bean("chsProducerConfig")
  @Primary
  public ProducerConfig producerConfig() {
    return new ProducerConfig(
        buildProducer(primaryBootstrap, primarySaslJaasConfig),
        buildProducer(drBootstrap, drSaslJaasConfig)
    );
  }

  /**
   * Creates an {@link EventSender} for sending messages to EDA topics.
   * <p>
   * Uses the provided {@link ProducerConfig} to configure the Kafka producer
   * for sending messages asynchronously to the EDA event feed.
   * </p>
   *
   * @param producerConfig the {@link ProducerConfig} used to configure the sender
   * @return a configured {@link EventSender} instance for sending EDA messages
   */
  @Bean
  public EventSender<String, String> edaSender(final ProducerConfig producerConfig) {
    return new EdaSender(producerConfig);
  }

  /**
   * Protects Spring MVC's ObjectMapper from being overridden by fts-client's Jackson configuration.
   */
  @Bean
  public Jackson2ObjectMapperBuilderCustomizer jacksonCustomizer() {
    return builder -> builder
        .failOnUnknownProperties(false)
        .featuresToDisable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
  }

  private KafkaProducer<String, String> buildProducer(final String bootstrap,
      final String saslJaasConfig) {
    final Properties properties = new Properties();
    properties.put(CommonClientConfigs.CLIENT_ID_CONFIG, clientWid + "-producer-v1-" + UUID.randomUUID());
    properties.put(CommonClientConfigs.BOOTSTRAP_SERVERS_CONFIG, bootstrap);
    properties.put(CommonClientConfigs.SECURITY_PROTOCOL_CONFIG, securityProtocol);
    properties.put(SaslConfigs.SASL_MECHANISM, saslMechanism);
    properties.put(SaslConfigs.SASL_JAAS_CONFIG, saslJaasConfig);
    properties.put(org.apache.kafka.clients.producer.ProducerConfig.BUFFER_MEMORY_CONFIG, bufferMemory);
    properties.put(org.apache.kafka.clients.producer.ProducerConfig.BATCH_SIZE_CONFIG, batchSize);
    properties.put(org.apache.kafka.clients.producer.ProducerConfig.LINGER_MS_CONFIG, lingerMs);
    properties.put(org.apache.kafka.clients.producer.ProducerConfig.ENABLE_IDEMPOTENCE_CONFIG, enableIdempotence);
    properties.put(org.apache.kafka.clients.producer.ProducerConfig.ACKS_CONFIG, acks);
    properties.put(org.apache.kafka.clients.producer.ProducerConfig.COMPRESSION_TYPE_CONFIG, compressionType);
    properties.put(CommonClientConfigs.REQUEST_TIMEOUT_MS_CONFIG, requestTimeout);
    properties.put(org.apache.kafka.clients.producer.ProducerConfig.DELIVERY_TIMEOUT_MS_CONFIG, deliveryTimeout);
    return new KafkaProducer<>(properties, new StringSerializer(), new StringSerializer());
  }
}
