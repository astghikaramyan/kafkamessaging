package com.xyz.enterprise.fts.producer;

import java.util.EnumMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.xyz.enterprise.fts.config.FtsConfig;
import com.xyz.enterprise.fts.dao.FtsDAO;
import com.xyz.enterprise.fts.model.ClusterPointer;
import com.xyz.enterprise.fts.model.FtsResponse;

public class FtsProducer {

  private static final Logger logger = LoggerFactory.getLogger(FtsProducer.class);

  private final Map<ClusterPointer, KafkaProducer<String, String>> producerMap = new EnumMap<>(ClusterPointer.class);
  private final FtsDAO ftsDAO;
  private final FtsConfig ftsConfig;
  private final ScheduledExecutorService ftsScheduler;

  /**
   * Constructs an FtsProducer.
   *
   * @param ftsDAO            the FTS DAO (must not be null)
   * @param delay             the delay in seconds for FTS polling
   * @param primaryProducer   the primary Kafka producer (must not be null)
   * @param secondaryProducer the secondary Kafka producer (must not be null)
   */
  public FtsProducer(final FtsDAO ftsDAO, final int delay,
      final KafkaProducer<String, String> primaryProducer,
      final KafkaProducer<String, String> secondaryProducer) {
    this.ftsDAO = Objects.requireNonNull(ftsDAO, "ftsDAO must not be null");
    this.producerMap.put(ClusterPointer.PRIMARY,
        Objects.requireNonNull(primaryProducer, "primaryProducer must not be null"));
    this.producerMap.put(ClusterPointer.SECONDARY,
        Objects.requireNonNull(secondaryProducer, "secondaryProducer must not be null"));

    this.ftsConfig = new FtsConfig(null);
    this.ftsScheduler = Executors.newSingleThreadScheduledExecutor();
    this.ftsScheduler.scheduleAtFixedRate(this::refreshFtsConfig, 0, delay, TimeUnit.SECONDS);

    Runtime.getRuntime().addShutdownHook(new Thread(this::shutdown));
  }

  /**
   * Returns the active Kafka producer based on the current FTS config.
   *
   * @return the active KafkaProducer, or null if not available
   */
  public KafkaProducer<String, String> getProducer() {
    ClusterPointer cluster = ftsConfig.getCluster();
    KafkaProducer<String, String> producer = producerMap.get(cluster);
    if (producer == null) {
      logger.error("No KafkaProducer found for cluster: {}", cluster);
    }
    return producer;
  }

  /**
   * Shuts down the FtsProducer and flushes all Kafka producers.
   */
  public void shutdown() {
    logger.info("Shutting down scheduler and flushing producers.");
    producerMap.values().forEach(KafkaProducer::flush);
    ftsScheduler.shutdown();
  }

  private void refreshFtsConfig() {
    Optional<FtsResponse> ftsResponse = ftsDAO.queryFts();
    if (ftsResponse.isEmpty()) {
      return;
    }
    try {
      ftsConfig.parseKafkaConfig(ftsResponse.get().getUnwrappedValue());
    } catch (IllegalArgumentException e) {
      logger.error("Error parsing FTS response: {}", e.getMessage());
    }
    logger.info("Producer FTS check - active cluster: {}", ftsConfig.getCluster());
  }
}