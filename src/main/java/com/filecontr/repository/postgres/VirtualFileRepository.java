package com.filecontr.repository.postgres;

import java.sql.ResultSet;
import java.util.Arrays;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import com.filecontr.service.virtual_files.IVirtualFile;
import com.filecontr.utils.functional_classes.id.IIdentificator;

@Repository
public class VirtualFileRepository {
  private final NamedParameterJdbcTemplate jdbcTemplate;
  private final DictionarySQL dictionary;

  public VirtualFileRepository(NamedParameterJdbcTemplate template, DictionarySQL dictionarySQL) {
    this.jdbcTemplate = template;
    this.dictionary = dictionarySQL;
  }

  public Optional<IVirtualFile> getVirtualFileById(Function<ResultSet, Optional<IVirtualFile>> exec, Long... ids) {
    var value = Arrays.stream(ids).map(id -> id.toString()).collect(Collectors.joining(","));
    var params = new MapSqlParameterSource("id", value);
    return jdbcTemplate.query(dictionary.GET_VIRTUAL_FILE(), params, exec::apply);
  }

  public Optional<Map<IIdentificator, IIdentificator>> getAllRelations(Function<ResultSet, Optional<Map<IIdentificator, IIdentificator>>> exec) {
    return jdbcTemplate.query(dictionary.GET_RELATIONS(), exec::apply);
  }

  public Optional<IIdentificator> getParentSingle(Function<ResultSet, Optional<IIdentificator>> exec, Long id) {
    var params = new MapSqlParameterSource("id", id);
    return jdbcTemplate.query(dictionary.GET_PARENT(), params, exec::apply);
  }

  public Optional<IIdentificator[]> getChildrenSingle(Function<ResultSet, Optional<IIdentificator[]>> exec, Long id) {
    var params = new MapSqlParameterSource("id", id);
    return jdbcTemplate.query(dictionary.GET_PARENT(), params, exec::apply);
  }

  public Optional<Map<IIdentificator, IIdentificator>> getParentsMap(Function<ResultSet, Optional<Map<IIdentificator, IIdentificator>>> exec, Long... ids) {
    var value = Arrays.stream(ids).map(id -> id.toString()).collect(Collectors.joining(","));
    var params = new MapSqlParameterSource("id", value);
    return jdbcTemplate.query(dictionary.GET_PARENT(), params, exec::apply);
  }

  public Optional<Map<IIdentificator, IIdentificator[]>> getChildrenMap(Function<ResultSet, Optional<Map<IIdentificator, IIdentificator[]>>> exec, Long... ids) {
    var value = Arrays.stream(ids).map(id -> id.toString()).collect(Collectors.joining(","));
    var params = new MapSqlParameterSource("id", value);
    return jdbcTemplate.query(dictionary.GET_CHILD(), params, exec::apply);
  }

  public Boolean addVirtualFile(Function<ResultSet, Optional<IVirtualFile>> exec, IVirtualFile... files) {
    var stream = Arrays.stream(files);
    String valuesRelations = stream
      .map(a -> String.format(
        "(%d, %d)",
        a.getId().toLong(),
        a.getParentId().get().toLong()
        )
      )
      .collect(Collectors.joining(", "));
    String valuesProperties = stream
      .map(a -> String.format(
        "(%d, %d, %d)",
        a.getId().toLong(),
        a.getContent().getFileData().parentId().get().toLong(),
        a.getContent().getCreationTime()
        )
      )
      .collect(Collectors.joining(", "));
    var params = new MapSqlParameterSource();
    params.addValue("valuesProperties", valuesProperties);
    params.addValue("valuesRelations", valuesRelations);
    jdbcTemplate.update(dictionary.ADD_VIRTUAL_FILE(), params);
    return true;
  }

  public Boolean deleteVirtualFile(Long... ids) {
    var value = Arrays.stream(ids).map(id -> id.toString()).collect(Collectors.joining(","));
    var params = new MapSqlParameterSource("id", value);
    jdbcTemplate.update(dictionary.ADD_VIRTUAL_FILE(), params);
    return true;
  }
}
