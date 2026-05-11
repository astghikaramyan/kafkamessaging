## FTS Client Library
## Overview
Library to leverage FTS logic for EDA clients.

## Features
This library will provide the ability for:
* Consumers to consume events from active or DR cluster.
* REST Producers to produce events to active or DR cluster.

## Prerequisites
You will need to request a new FTS flag for your application in order to retrieve it from the FTS service. Please reach out to MW Support <ESITargaryens@xyz.com>. Response from FTS will look like this:
```json
{
  "code": "eve_producer_active_cluster",
  "name": "EDA Producer Switch",
  "value": {
    "type": "StringValue",
    "value": "kafka-server:SECONDARY"
  },
  "cached": true,
  "series": null,
  "unwrappedValue": "kafka-server:SECONDARY"
}
```

## Usage
Include the latest version as a dependency in your Maven or Gradle file using the latest available version compatible with your project. 

```xml
<dependency>
  <groupId>com.xyz.enterprise.fts</groupId>
  <artifactId>fts-client</artifactId>
  <version>${fts-client.version}</version>
</dependency>
```
You may need to add exclusions to this dependency if the library dependencies conflict with your other required libraries.

Build a CloseableHttpClient to be used for FTS calls by using the default builder:

```java
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;

import com.xyz.enterprise.fts.builder.FtsHttpClientBuilder;

CloseableHttpClient httpClient = FtsHttpClientBuilder.buildDefaultHttpClient();
```

You can override the default configuration by using any of the builder methods available in the `FttsHttpClientBuilder` class.

### Producers
Build the PRIMARY and SECONDARY producers:

```java
import org.apache.kafka.clients.producer.KafkaProducer;

import com.xyz.enterprise.fts.builder.KafkaClientBuilder;

Properties producerProperties = new Properties();
producerProperties.put("bootstrap.servers","localhost:9092");
producerProperties.put("client.id","producer-primary-v1");
producerProperties.put("sasl.mechanism","PLAIN");
//Remaining properties

KafkaProducer<String, String> primaryProducer = KafkaClientBuilder.buildKafkaProducer(producerProperties);

Properties producerProperties = new Properties();
producerProperties.put("bootstrap.servers","localhost:9093");
producerProperties.put("client.id","producer-secondary-v1");
producerProperties.put("sasl.mechanism","PLAIN");
//Remaining properties

KafkaProducer<String, String> secondaryProducer = KafkaClientBuilder.buildKafkaProducer(producerProperties);
```

Build the FTS Producer request:

```java
import org.apache.hc.core5.http.ClassicHttpRequest;

import com.xyz.enterprise.fts.builder.FtsHttpClientBuilder;

ClassicHttpRequest ftsRequest = FtsHttpClientBuilder.buildProducerFtsRequest(
    "http://localhost:8080/v1/api/fts", "eve", null);
```

Initialize the FtsDaoImpl:

```java


import com.fasterxml.jackson.databind.ObjectMapper;
import com.xyz.enterprise.fts.builder.FtsHttpClientBuilder;
import com.xyz.enterprise.fts.producer.dao.FtsDAO;
import com.xyz.enterprise.fts.producer.dao.impl.FtsDAOImpl;

FtsDAO ftsDAOImpl = new FtsDAOImpl(httpClient, new ObjectMapper(), ftsRequest,
    FtsHttpClientBuilder.buildResponseHandler());
```

Initialize the FtsProducer and pass the FtsDAOImpl, the delay between calls to FTS and the producers:

```java
import com.xyz.enterprise.fts.producer.FtsProducer;

FtsProducer ftsProducer = new FtsProducer(ftsDAOImpl, 60, primaryProducer, secondaryProducer);
```

Above client will run every 60 seconds and will produce events to the active cluster.

### Streams
Build the topology for your app with the source and any processors / sinks you need. Keep in mind that primary and secondary topologies might be different for your app (i.e. each needs to push events to a topic in one cluster or the other).
```java
import org.apache.kafka.streams.Topology;

Topology topology = new Topology().addSource("test", "test-topic");
```

Build the PRIMARY and SECONDARY properties for the Kafka Streams:
```java
import org.apache.kafka.clients.producer.KafkaProducer;

import com.xyz.enterprise.fts.builder.KafkaClientBuilder;

Properties primaryStreamProperties = new Properties();
producerProperties.put("bootstrap.servers","localhost:9092");
producerProperties.put("application.id","primary-stream-v1");
producerProperties.put("sasl.mechanism","PLAIN");
//Remaining properties

Properties secondaryStreamProperties = new Properties();
producerProperties.put("bootstrap.servers","localhost:9093");
producerProperties.put("client.id","secondary-stream-v1");
producerProperties.put("sasl.mechanism","PLAIN");
//Remaining properties
```

Build the FTS Stream request:

```java
import org.apache.hc.core5.http.ClassicHttpRequest;

import com.xyz.enterprise.fts.builder.FtsHttpClientBuilder;

ClassicHttpRequest ftsRequest = FtsHttpClientBuilder.buildStreamFtsRequest(
    "http://localhost:8080/v1/api/fts", "ebt", null);
```

Initialize the FtsDaoImpl:

```java


import com.fasterxml.jackson.databind.ObjectMapper;
import com.xyz.enterprise.fts.builder.FtsHttpClientBuilder;
import com.xyz.enterprise.fts.producer.dao.FtsDAO;
import com.xyz.enterprise.fts.producer.dao.impl.FtsDAOImpl;

FtsDAO ftsDAOImpl = new FtsDAOImpl(httpClient, new ObjectMapper(), ftsRequest,
    FtsHttpClientBuilder.buildResponseHandler());
```

Initialize the primary and secondary Kafka Streams and configure them with the topology and properties:

```java

EdaKafkaStreams primaryStreams = EdaKafkaStreams.getInstance(ClusterPointer.PRIMARY);
primaryStreams.configure(topology, primaryStreamProperties);

EdaKafkaStreams secondaryStreams = EdaKafkaStreams.getInstance(ClusterPointer.SECONDARY);
secondaryStreams.configure(topology, secondaryStreamProperties);
```

Initialize the FtsStream and pass the FtsDAOImpl, the region where the stream app is running and the delay between calls to FTS:
```java


FtsStream ftsStream = new FtsStream(ftsDAOImpl, "us_east_1", 60);
```

Above client will run every 60 seconds and will consume from the active stream if the FTS region matches "us_east_1".
