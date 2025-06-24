package com.filecontr.repository.postgres;

import java.util.Arrays;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Repository;

import com.filecontr.service.virtual_files.IVirtualFile;

@Repository
public class VirtualFileRepository {
  final BiFunction<String, MapSqlParameterSource, SqlRowSet> biQuery;
  final Function<String, SqlRowSet> oneQuery;
  final DictionarySQL dictionary;

  protected VirtualFileRepository(
    BiFunction<String, MapSqlParameterSource, SqlRowSet> biQuery,
    Function<String, SqlRowSet> oneQuery,
    DictionarySQL dictionarySQL
  ) {
    this.biQuery = biQuery;
    this.oneQuery = oneQuery;
    this.dictionary = dictionarySQL;
  }

  public VirtualFileRepository(NamedParameterJdbcTemplate template, DictionarySQL dictionarySQL) {
    this.biQuery = (String str, MapSqlParameterSource map) -> {
      return template.queryForRowSet(str, map);
    };
    var params = new MapSqlParameterSource();
    this.oneQuery = (String str) -> {
      return template.queryForRowSet(str, params);
    };
    this.dictionary = dictionarySQL;
  }

  public SqlRowSet getVirtualFileById(Long... ids) {
    var value = Arrays.stream(ids).map(id -> id.toString()).collect(Collectors.joining(","));
    var params = new MapSqlParameterSource("id", value);
    return biQuery.apply(dictionary.GET_VIRTUAL_FILE(), params);
  }

  @Deprecated
  public SqlRowSet getAllRelations() {
    return oneQuery.apply(dictionary.GET_RELATIONS());
  }

  public SqlRowSet getParents(Long... ids) {
    var value = Arrays.stream(ids).map(id -> id.toString()).collect(Collectors.joining(","));
    var params = new MapSqlParameterSource("id", value);
    return biQuery.apply(dictionary.GET_PARENT(), params);
  }

  public SqlRowSet getChildren(Long... ids) {
    var value = Arrays.stream(ids).map(id -> id.toString()).collect(Collectors.joining(","));
    var params = new MapSqlParameterSource("id", value);
    return biQuery.apply(dictionary.GET_CHILD(), params);
  }

  public Boolean addVirtualFile(IVirtualFile... files) {
    String valuesRelations = Arrays.stream(files)
      .map(a -> String.format(
        "(%d, %d)",
        a.getId().toLong(),
        a.getContent().getFileData().parentId().isPresent()? a.getContent().getFileData().parentId().get().toLong() : a.getId().toLong()
        )
      )
      .collect(Collectors.joining(", "));
    String valuesProperties = Arrays.stream(files)
      .map(a -> String.format(
        "(%d, %s, %d)",
        a.getId().toLong(),
        a.getContent().getFileData().type().isPresent()? a.getContent().getFileData().type().get() : "/NONE",
        a.getContent().getCreationTime()
        )
      )
      .collect(Collectors.joining(", "));
    var params = new MapSqlParameterSource();
    params.addValue("valuesProperties", valuesProperties);
    params.addValue("valuesRelations", valuesRelations);
    biQuery.apply(dictionary.ADD_VIRTUAL_FILE(), params);
    return true;
  }

  public Boolean deleteVirtualFile(Long... ids) {
    var value = Arrays.stream(ids).map(id -> id.toString()).collect(Collectors.joining(","));
    var params = new MapSqlParameterSource("id", value);
    biQuery.apply(dictionary.ADD_VIRTUAL_FILE(), params);
    return true;
  }
}
