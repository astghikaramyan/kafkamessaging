package com.xyz.enterprise.fts.stream;

import java.util.EnumMap;
import java.util.Map;
import java.util.Properties;

import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.Topology;

import com.xyz.enterprise.fts.model.ClusterPointer;

public class EdaKafkaStreams {

  private static final Map<ClusterPointer, EdaKafkaStreams> STREAMS = new EnumMap<>(ClusterPointer.class);

  private Topology topology;
  private Properties streamsProperties;
  private KafkaStreams kafkaStream;

  private EdaKafkaStreams() {}

  /**
   * Returns a singleton instance of Streams for the specified type.
   *
   * @param type the type of Streams (PRIMARY or SECONDARY)
   * @return a singleton instance of Streams
   */
  public static EdaKafkaStreams getInstance(ClusterPointer type) {
    return STREAMS.computeIfAbsent(type, t -> new EdaKafkaStreams());
  }

  /**
   * Sets the instance of EdaKafkaStreams for the specified type.
   *
   * @param type the type of Streams (PRIMARY or SECONDARY)
   * @param instance the EdaKafkaStreams instance to set
   */
  public static void setInstance(ClusterPointer type, EdaKafkaStreams instance) {
    STREAMS.put(type, instance);
  }

  /**
   * Configures the Streams instance with the given topology and properties.
   *
   * @param topology the Kafka Streams topology
   * @param streamsProperties the properties for Kafka Streams
   */
  public synchronized void configure(Topology topology, Properties streamsProperties) {
    this.topology = topology;
    this.streamsProperties = streamsProperties;
  }

  /**
   * Returns the KafkaStreams instance, creating it if necessary.
   *
   * @return the KafkaStreams instance
   * @throws IllegalStateException if topology or streamsProperties are not set
   */
  public synchronized KafkaStreams getKafkaStream() throws IllegalStateException {
    if (kafkaStream == null) {
      if (topology == null || streamsProperties == null) {
        throw new IllegalStateException("Kafka Streams instance is not configured. "
            + "Topology and StreamsProperties must be set before getting KafkaStreams instance.");
      }
      kafkaStream = new KafkaStreams(topology, streamsProperties);
    }
    return kafkaStream;
  }

  /**
   * Starts the Kafka Streams instance if it is not already running.
   */
  public synchronized void start() throws IllegalStateException {
    getKafkaStream();
    if (!isRunning()) {
      kafkaStream.start();
    }
  }

  /**
   * Stops the Kafka Streams instance if it is running.
   */
  public synchronized void close() {
    if (kafkaStream != null) {
      kafkaStream.close();
      kafkaStream = null;
    }
  }

  /**
   * Checks if the Kafka Streams instance is currently running.
   *
   * @return true if the Kafka Streams instance is running or rebalancing, false otherwise
   */
  public synchronized boolean isRunning() {
    return kafkaStream != null && kafkaStream.state().isRunningOrRebalancing();
  }

  /**
   * Clears all instances of EdaKafkaStreams.
   */
  public static void clearInstances() {
    STREAMS.clear();
  }
}