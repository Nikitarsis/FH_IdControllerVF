package com.filecontr.repository.postgres;

import java.sql.ResultSet;
import java.util.Optional;
import java.util.function.Function;

import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class VirtualFileRepository {
  private final NamedParameterJdbcTemplate jdbcTemplate;

  VirtualFileRepository(NamedParameterJdbcTemplate template) {
    this.jdbcTemplate = template;
  }

  public Optional<String> getById(int id, Function<ResultSet, Optional<String>> exec) {
    var params = new MapSqlParameterSource("id", id);
    return jdbcTemplate.query("null", params, exec::apply);
  }
}
