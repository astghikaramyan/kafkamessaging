package com.xyz.enterprise.offer.event.feed.kafka.sender;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import org.apache.kafka.clients.producer.Callback;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.apache.kafka.clients.producer.internals.ErrorLoggingCallback;

public interface EventSender<K, V> {

  Future<RecordMetadata> send(String topic, K key, V value);

  Future<RecordMetadata> send(String topic, V value);

  default RecordMetadata blockingSend(String topic, K key, V payload) throws SendException {
    try {
      return send(topic, key, payload).get();
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
      throw new SendException(e);
    } catch (ExecutionException e) {
      throw new SendException(e.getCause());
    }
  }

  default Callback senderCallback(final String topic, final byte[] k, final byte[] v) {
    return new ErrorLoggingCallback(
        topic,
        k,
        v,
        true
    );
  }
}
