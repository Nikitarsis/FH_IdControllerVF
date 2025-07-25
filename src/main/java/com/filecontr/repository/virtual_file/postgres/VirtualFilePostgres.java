package com.filecontr.repository.virtual_file.postgres;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Repository;

import com.filecontr.repository.virtual_file.IVirtualFileRepository;
import com.filecontr.service.virtual_files.IVirtualFile;
import com.filecontr.utils.adapters.logger.ILogger;
import com.filecontr.utils.functional_classes.id.IIdentificator;

@Repository("LongtimeDatabase")
public class VirtualFilePostgres implements IVirtualFileRepository<Map<String,String>>{
  final BiFunction<String, MapSqlParameterSource, List<Map<String, String>>> biQuery;
  final Function<String, List<Map<String, String>>> oneQuery;
  final ILogger logger;
  final DictionarySQL dictionary;

  protected VirtualFilePostgres(
    BiFunction<String, MapSqlParameterSource, List<Map<String, String>>> biQuery,
    Function<String, List<Map<String, String>>> oneQuery,
    Function<Class<?>, ILogger> loggerProducer,
    DictionarySQL dictionarySQL
  ) {
    this.biQuery = biQuery;
    this.logger = loggerProducer.apply(this.getClass());
    this.oneQuery = oneQuery;
    this.dictionary = dictionarySQL;
  }

  @Autowired
  public VirtualFilePostgres(
    NamedParameterJdbcTemplate template,
    Function<Class<?>, ILogger> loggerProducer,
    DictionarySQL dictionarySQL
  ) {
    this.biQuery = (String str, MapSqlParameterSource map) -> {
      return rowSetToMap(template.queryForRowSet(str, map));
    };
    var params = new MapSqlParameterSource();
    this.oneQuery = (String str) -> {
      return rowSetToMap(template.queryForRowSet(str, params));
    };
    this.dictionary = dictionarySQL;
    this.logger = loggerProducer.apply(this.getClass());
  }

  protected List<Map<String, String>> rowSetToMap(SqlRowSet resultSet) {
    ArrayList<Map<String, String>> ret = new ArrayList<>();
    while (resultSet.next()) {
      Map<String, String> valueMap = new HashMap<>();
      var columnCount = resultSet.getMetaData().getColumnCount();
      for (var i = 0; i < columnCount; i++) {
        var key = resultSet.getMetaData().getColumnName(i);
        var value = resultSet.getString(i);
        valueMap.put(key, value);
      }
      ret.add(valueMap);
    }
    return ret;
  }

  @Override
  public List<Optional<IVirtualFile>> getVirtualFileById(
      Function<Map<String,String>, Optional<IVirtualFile>> converter, 
      IIdentificator... ids
    ) {
    var value = Arrays.stream(ids).map(id -> id.toLong().toString()).collect(Collectors.joining(","));
    logger.debug(String.format("Getting VirtualFile from PostgreSQL; ids: %s", value));
    var params = new MapSqlParameterSource("id", value);
    return biQuery.apply(dictionary.GET_VIRTUAL_FILE(), params).stream().map(converter::apply).toList();
  }

  @Deprecated
  public List<Map<String, String>> getAllRelations() {
    logger.debug("Getting all Relations from PostgreSQL");
    logger.warn("Deprecated method");
    return oneQuery.apply(dictionary.GET_RELATIONS());
  }

  public List<Map<String, String>> getParents(IIdentificator... ids) {
    var value = Arrays.stream(ids).map(id -> id.toLong().toString()).collect(Collectors.joining(","));
    logger.debug(String.format("Getting parents from PostgreSQL; ids: %s", value));
    var params = new MapSqlParameterSource("id", value);
    return biQuery.apply(dictionary.GET_PARENT(), params);
  }

  public List<Map<String, String>> getChildren(IIdentificator... ids) {
    var value = Arrays.stream(ids).map(id -> id.toLong().toString()).collect(Collectors.joining(","));
    logger.debug(String.format("Getting children from PostgreSQL; ids: %s", value));
    var params = new MapSqlParameterSource("id", value);
    return biQuery.apply(dictionary.GET_CHILD(), params);
  }

  @Override
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
    logger.debug(String.format("Add VirtualFile to PostgreSQL; ids: %s;%s", valuesProperties, valuesRelations));
    params.addValue("valuesProperties", valuesProperties);
    params.addValue("valuesRelations", valuesRelations);
    biQuery.apply(dictionary.ADD_VIRTUAL_FILE(), params);
    return true;
  }

  @Override
  public Boolean deleteVirtualFile(IIdentificator... ids) {
    var value = Arrays.stream(ids).map(id -> id.toLong().toString()).collect(Collectors.joining(","));
    logger.debug(String.format("Removing VirtualFile from PostgreSQL; ids: %s", value));
    var params = new MapSqlParameterSource("id", value);
    biQuery.apply(dictionary.ADD_VIRTUAL_FILE(), params);
    return true;
  }

  @Override
  public Boolean setType(IIdentificator id, String type) {
    logger.debug(String.format(""));
    var params = new MapSqlParameterSource("id", id.toLong().toString());
    params.addValue("type", "type = " + type);
    biQuery.apply(dictionary.UPDATE_VIRTUAL_FILE(), params);
    return true;
  }
}
