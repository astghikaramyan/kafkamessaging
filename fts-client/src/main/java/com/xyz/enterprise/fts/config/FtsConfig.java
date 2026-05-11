package com.xyz.enterprise.fts.config;

import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.xyz.enterprise.fts.model.ClusterPointer;
import com.xyz.enterprise.fts.model.RegionPointer;

public class FtsConfig {

  private static final Logger logger = LoggerFactory.getLogger(FtsConfig.class);

  private static final String KAFKA_SERVER = "kafka-server";
  private static final String KAFKA_CLIENT = "kafka-client";

  private final String location;

  private ClusterPointer cluster = ClusterPointer.PRIMARY;
  private RegionPointer region = RegionPointer.UNKNOWN;

  /**
   * Constructor for FtsConfig.
   *
   * @param location the location of the FTS
   */
  public FtsConfig(String location) {
    this.location = location;
  }

  /**
   * Parses the input string to set the cluster and region.
   *
   * @param input the input string containing Kafka configuration
   */
  public void parseKafkaConfig(String input) {
    if (input == null || input.isEmpty()) {
      throw new IllegalArgumentException("Input string cannot be null or empty");
    }

    Map<String, String> config = Arrays.stream(input.split(","))
        .map(s -> s.split(":"))
        .filter(a -> a.length == 2)
        .collect(Collectors.toMap(a -> a[0], a -> a[1]));

    setCluster(ClusterPointer.of(config.get(KAFKA_SERVER)));
    setRegion(RegionPointer.of(config.get(KAFKA_CLIENT)));
  }

  public boolean isPrimaryActive() {
    return cluster == ClusterPointer.PRIMARY;
  }

  public boolean isSecondaryActive() {
    return cluster == ClusterPointer.SECONDARY;
  }

  public boolean isRegionUnknown() {
    return region == RegionPointer.UNKNOWN;
  }

  public boolean isRegionActive() {
    return region == RegionPointer.ALL || location.equalsIgnoreCase(region.name());
  }

  public ClusterPointer getCluster() {
    return cluster;
  }

  public RegionPointer getRegion() {
    return region;
  }

  private void setCluster(ClusterPointer clusterPointer) {
    if (clusterPointer == ClusterPointer.UNKNOWN) {
      logger.warn("ClusterPointer is unknown. Couldn't parse the cluster from the FTS response. Skipping cluster update.");
      return;
    }
    this.cluster = clusterPointer;
  }

  private void setRegion(RegionPointer regionPointer) {
    if (regionPointer == RegionPointer.UNKNOWN) {
      logger.warn("RegionPointer is unknown. Couldn't parse the region from the FTS response. Skipping region update.");
      return;
    }
    this.region = regionPointer;
  }
}
