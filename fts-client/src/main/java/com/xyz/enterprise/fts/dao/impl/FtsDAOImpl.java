package com.xyz.enterprise.fts.dao.impl;

import java.util.Optional;

import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.core5.http.ClassicHttpRequest;
import org.apache.hc.core5.http.io.HttpClientResponseHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.xyz.enterprise.fts.dao.FtsDAO;
import com.xyz.enterprise.fts.model.FtsResponse;

public class FtsDAOImpl implements FtsDAO {

  private static final Logger LOGGER = LoggerFactory.getLogger(FtsDAOImpl.class);

  private final ClassicHttpRequest ftsRequest;
  private final CloseableHttpClient httpClient;
  private final HttpClientResponseHandler<String> responseHandler;
  private final ObjectMapper objectMapper;

  /**
   * Constructor for FtsDAOImpl.
   *
   * @param httpClient the HTTP client
   * @param ftsRequest the FTS request
   * @param objectMapper the object mapper
   * @param responseHandler the response handler
   */
  public FtsDAOImpl(final CloseableHttpClient httpClient, final ClassicHttpRequest ftsRequest,
      final ObjectMapper objectMapper, final HttpClientResponseHandler<String> responseHandler) {
    this.ftsRequest = ftsRequest;
    this.httpClient = httpClient;
    this.responseHandler = responseHandler;
    this.objectMapper = objectMapper;
  }

  @Override
  public Optional<FtsResponse> queryFts() {
    try {
      LOGGER.debug("Querying FTS with headers ({}) and endpoint ({})", ftsRequest.getHeaders(),
          ftsRequest.getRequestUri());
      String ftsResponse = this.httpClient.execute(ftsRequest, responseHandler);
      LOGGER.debug("FTS response : {}", ftsResponse);
      FtsResponse response = objectMapper.readValue(ftsResponse, FtsResponse.class);
      return Optional.of(response);
    } catch (Exception e) {
      LOGGER.warn("Failed to retrieve active region/cluster from FTS - {}", e.getMessage());
      return Optional.empty();
    }
  }
}
