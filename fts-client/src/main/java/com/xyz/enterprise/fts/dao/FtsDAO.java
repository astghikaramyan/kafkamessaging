package com.xyz.enterprise.fts.dao;

import java.util.Optional;

import com.xyz.enterprise.fts.model.FtsResponse;

public interface FtsDAO {
  /**
   * Queries the FTS (Feature Toggle Service).
   *
   * @return an Optional containing the FTS response if successful, or an empty Optional if an error occurs
   */
  Optional<FtsResponse> queryFts();
}
