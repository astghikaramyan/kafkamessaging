package com.xyz.enterprise.fts.builder;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.concurrent.TimeUnit;

import org.apache.hc.client5.http.classic.methods.HttpGet;
import org.apache.hc.client5.http.config.ConnectionConfig;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClientBuilder;
import org.apache.hc.client5.http.impl.io.PoolingHttpClientConnectionManager;
import org.apache.hc.core5.http.ClassicHttpRequest;
import org.apache.hc.core5.http.Header;
import org.apache.hc.core5.http.io.HttpClientResponseHandler;
import org.apache.hc.core5.http.io.entity.EntityUtils;
import org.apache.hc.core5.net.URIBuilder;

public class FtsHttpClientBuilder {

  private static final String PRODUCER_ACTIVE_SEGMENT = "_producer_active_cluster";
  private static final String STREAM_ACTIVE_SEGMENT = "_eda_switch";

  private FtsHttpClientBuilder() {}

  /**
   * Builds a default HTTP client with a connection manager and default connection configuration.
   *
   * @return the default HTTP client
   */
  public static CloseableHttpClient buildDefaultHttpClient() {
    PoolingHttpClientConnectionManager connectionManager = new PoolingHttpClientConnectionManager();
    connectionManager.setMaxTotal(10);
    connectionManager.setDefaultMaxPerRoute(10);

    final ConnectionConfig connectionConfig = ConnectionConfig.custom()
        .setSocketTimeout(2, TimeUnit.SECONDS)
        .setConnectTimeout(5, TimeUnit.SECONDS)
        .build();
    connectionManager.setDefaultConnectionConfig(connectionConfig);

    return HttpClientBuilder.create()
        .setConnectionManager(connectionManager)
        .build();
  }

  /**
   * Builds a custom HTTP client with the specified connection manager.
   *
   * @param connectionManager the connection manager
   * @return the custom HTTP client
   */
  public static CloseableHttpClient buildHttpClient(
      PoolingHttpClientConnectionManager connectionManager) {
    return HttpClientBuilder.create()
        .setConnectionManager(connectionManager)
        .build();
  }

  /**
   * Builds a connection configuration with the specified socket and connection timeouts.
   *
   * @param socketTimeout    the socket timeout in seconds
   * @param connectTimeout   the connection timeout in seconds
   * @return the connection configuration
   */
  public static ConnectionConfig buildConnectionConfig(int socketTimeout, int connectTimeout) {
    return ConnectionConfig.custom()
        .setSocketTimeout(socketTimeout, TimeUnit.SECONDS)
        .setConnectTimeout(connectTimeout, TimeUnit.SECONDS)
        .build();
  }

  /**
   * Builds a pooling HTTP client connection manager with the specified connection configuration,
   * maximum total connections, and default maximum connections per route.
   *
   * @param connectionConfig    the connection configuration
   * @param maxTotal           the maximum total connections
   * @param defaultMaxPerRoute  the default maximum connections per route
   * @return the pooling HTTP client connection manager
   */
  public static PoolingHttpClientConnectionManager buildPoolingHttpClientConnectionManager(
      ConnectionConfig connectionConfig,
      int maxTotal, int defaultMaxPerRoute) {
    PoolingHttpClientConnectionManager connectionManager = new PoolingHttpClientConnectionManager();
    connectionManager.setMaxTotal(maxTotal);
    connectionManager.setDefaultMaxPerRoute(defaultMaxPerRoute);
    connectionManager.setDefaultConnectionConfig(connectionConfig);
    return connectionManager;
  }

  /**
   * Builds a request to the FTS producer endpoint with the specified base path, client ID, and headers.
   *
   * @param ftsBasePath  the base path of the FTS endpoint
   * @param clientWid    the client ID
   * @param headers      the headers to include in the request
   * @return the HTTP GET request
   * @throws URISyntaxException if the URI is malformed
   */
  public static ClassicHttpRequest buildProducerFtsRequest(String ftsBasePath, String clientWid,
      Header[] headers) throws URISyntaxException {
    final URI producerFtsEndpoint = new URIBuilder(ftsBasePath)
        .appendPathSegments(clientWid + PRODUCER_ACTIVE_SEGMENT)
        .build();
    ClassicHttpRequest request = new HttpGet(producerFtsEndpoint);
    request.setHeaders(headers);
    return request;
  }


  /**
   * Builds a request to the FTS stream endpoint with the specified base path, client ID, and headers.
   *
   * @param ftsBasePath  the base path of the FTS endpoint
   * @param clientWid    the client ID
   * @param headers      the headers to include in the request
   * @return the HTTP GET request
   * @throws URISyntaxException if the URI is malformed
   */
  public static ClassicHttpRequest buildStreamFtsRequest(String ftsBasePath, String clientWid,
      Header[] headers) throws URISyntaxException {
    final URI producerFtsEndpoint = new URIBuilder(ftsBasePath)
        .appendPathSegments(clientWid + STREAM_ACTIVE_SEGMENT)
        .build();
    ClassicHttpRequest request = new HttpGet(producerFtsEndpoint);
    request.setHeaders(headers);
    return request;
  }

  /**
   * Builds a request to the FTS endpoint with the specified endpoint and headers.
   *
   * @param endpoint  the FTS endpoint
   * @param headers   the headers to include in the request
   * @return the HTTP GET request
   */
  public static ClassicHttpRequest buildFtsRequest(String endpoint, Header[] headers) {
    ClassicHttpRequest request = new HttpGet(endpoint);
    request.setHeaders(headers);
    return request;
  }

  /**
   * Builds a response handler for the HTTP client that processes the response and returns the
   * response body as a string.
   *
   * @return the HTTP client response handler
   */
  public static HttpClientResponseHandler<String> buildResponseHandler() {
    return response -> {
      int status = response.getCode();
      if (status == 200) {
        return EntityUtils.toString(response.getEntity());
      } else {
        throw new RuntimeException("Unexpected response status: " + status);
      }
    };
  }
}
