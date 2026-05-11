package com.xyz.enterprise.fts.stream;

import java.util.Optional;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.apache.kafka.streams.errors.StreamsException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.xyz.enterprise.fts.config.FtsConfig;
import com.xyz.enterprise.fts.dao.FtsDAO;
import com.xyz.enterprise.fts.model.ClusterPointer;
import com.xyz.enterprise.fts.model.FtsResponse;

public class FtsStream {

  private static final Logger logger = LoggerFactory.getLogger(FtsStream.class);

  private final FtsDAO ftsDaoImpl;
  private final FtsConfig ftsConfig;
  private final EdaKafkaStreams primaryStream;
  private final EdaKafkaStreams secondaryStream;
  private final ScheduledExecutorService ftsScheduler;

  /**
   * Constructor for FtsStream.
   *
   * @param ftsDaoImpl      the FTS DAO
   * @param location        the location of the FTS
   * @param delay           the delay in seconds
   */
  public FtsStream(FtsDAO ftsDaoImpl, String location, int delay) {
    if (ftsDaoImpl == null) {
      throw new IllegalArgumentException("FtsDAO cannot be null");
    }
    this.ftsDaoImpl = ftsDaoImpl;
    this.ftsConfig = new FtsConfig(location);
    this.primaryStream = EdaKafkaStreams.getInstance(ClusterPointer.PRIMARY);
    this.secondaryStream = EdaKafkaStreams.getInstance(ClusterPointer.SECONDARY);

    ftsScheduler = Executors.newScheduledThreadPool(1);
    ftsScheduler.scheduleAtFixedRate(this::refreshFtsConfig, 0, delay, TimeUnit.SECONDS);

    Runtime.getRuntime().addShutdownHook(new Thread(this::shutdown));
  }

  /**
   * Shuts down the scheduler and closes both streams.
   */
  public void shutdown() {
    logger.info("Shutting down scheduler and closing streams.");
    primaryStream.close();
    secondaryStream.close();
    ftsScheduler.shutdown();
  }

  private void refreshFtsConfig() {
    Optional<FtsResponse> ftsResponse = ftsDaoImpl.queryFts();
    if (ftsResponse.isEmpty()) {
      return;
    }
    try {
      ftsConfig.parseKafkaConfig(ftsResponse.get().getUnwrappedValue());
    } catch (IllegalArgumentException e) {
      logger.error("Error parsing FTS response. {}", e.getMessage());
    }
    logger.debug("Active cluster : {}", ftsConfig.getCluster());
    logger.debug("Active region : {}", ftsConfig.getRegion());
    if (ftsConfig.isRegionUnknown()) {
      logger.warn("Region is unknown. Skipping streams start/stop.");
      return;
    }
    stopKafkaStreams();
    try {
      startKafkaStreams();
    } catch (Exception e) {
      logger.error("Error while starting the Kafka streams. {}", e.getMessage());
    }
  }

  private void startKafkaStreams() throws StreamsException, IllegalStateException {
    if (ftsConfig.isRegionActive()) {
      if (ftsConfig.isPrimaryActive()) {
        if (!primaryStream.isRunning()) {
          primaryStream.start();
          logger.info("Primary stream started.");
        } else {
          logger.debug("Primary stream already running.");
        }
      } else if (ftsConfig.isSecondaryActive()) {
        if (!secondaryStream.isRunning()) {
          secondaryStream.start();
          logger.info("Secondary stream started.");
        } else {
          logger.debug("Secondary stream already running.");
        }
      }
    } else {
      logger.debug("Region is inactive. Not starting any streams.");
    }
  }

  private void stopKafkaStreams() {
    if (primaryStream.isRunning() && (!ftsConfig.isRegionActive() || ftsConfig.isSecondaryActive())) {
      primaryStream.close();
      logger.info("Primary stream closed.");
    }
    if (secondaryStream.isRunning() && (!ftsConfig.isRegionActive() || ftsConfig.isPrimaryActive())) {
      secondaryStream.close();
      logger.info("Secondary stream closed.");
    }
  }
}
