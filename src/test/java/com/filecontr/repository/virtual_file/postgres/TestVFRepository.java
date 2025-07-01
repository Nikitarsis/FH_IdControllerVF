package com.filecontr.repository.virtual_file.postgres;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;

import com.filecontr.service.virtual_files.IVirtualFile;
import com.filecontr.utils.adapters.logger.AdapterLoggerFactory;
import com.filecontr.utils.functional_classes.id.IIdentificator;
import com.filecontr.utils.functional_classes.id.IdFactory;

public class TestVFRepository {

  VirtualFilePostgres getTestRepository(final Consumer<String> checkSQL, final Consumer<MapSqlParameterSource> checkParam, DictionarySQL dictionarySQL){
    BiFunction<String, MapSqlParameterSource, List<Map<String, String>>> biQuery = (sql, param) -> {
      checkSQL.accept(sql);
      checkParam.accept(param);
      return List.of(Map.of("a", "b"));
    };
    Function<String, List<Map<String, String>>> oneQuery = (sql) -> {
      checkSQL.accept(sql);
      return List.of(Map.of("a","b"));
    };
    return new VirtualFilePostgres(biQuery, oneQuery, AdapterLoggerFactory::getTestLogger, dictionarySQL);
  }

  Consumer<String> getSQLChecker(String expected) {
    return (sql) -> Assertions.assertEquals(expected, sql);
  }

  Consumer<MapSqlParameterSource> getEmptyParamChecker() {
    return (params) -> {};
  }

  Consumer<MapSqlParameterSource> getParamChecker(Map<String, String> map) {
    return (params) -> {
      params.getValues()
        .entrySet()
        .forEach(a -> Assertions.assertEquals(map.get(a.getKey()), a.getValue().toString()));
    };
  }

  Function<Map<String, String>, Optional<IVirtualFile>> getConverter() {
    return (map) -> {
      return Optional.of(IVirtualFile.getTestVF(IdFactory.createTestFactory()));
    };
  }

  @Test
  void testGetVirtualFileById() {
    Long[] longIds = {12l, 5l, 34l};
    var ids = Arrays.stream(longIds)
      .map(IdFactory::createIdFromLong)
      .toArray(IIdentificator[]::new);
    var dictionarySQL = DictionarySQL.parseFromResource().get();
    var checkParam = getParamChecker(Map.of("id", Arrays.stream(ids).map(a -> a.toString()).collect(Collectors.joining(","))));
    var checkerSQL = getSQLChecker(dictionarySQL.GET_VIRTUAL_FILE());
    var testRepo = getTestRepository(checkerSQL, checkParam, dictionarySQL);
    testRepo.getVirtualFileById(getConverter(), ids);
  }

  @Test
  void testGetParents() {
    Long[] longIds = {12l, 5l, 34l};
    var ids = Arrays.stream(longIds)
      .map(IdFactory::createIdFromLong)
      .toArray(IIdentificator[]::new);
    var dictionarySQL = DictionarySQL.parseFromResource().get();
    var checkParam = getParamChecker(Map.of("id", Arrays.stream(ids).map(a -> a.toString()).collect(Collectors.joining(","))));
    var checkerSQL = getSQLChecker(dictionarySQL.GET_PARENT());
    var testRepo = getTestRepository(checkerSQL, checkParam, dictionarySQL);
    testRepo.getParents(ids);
  }
  
  @Test
  void testGetChildren() {
    Long[] longIds = {12l, 5l, 34l};
    var ids = Arrays.stream(longIds)
      .map(IdFactory::createIdFromLong)
      .toArray(IIdentificator[]::new);
    var dictionarySQL = DictionarySQL.parseFromResource().get();
    var checkParam = getParamChecker(Map.of("id", Arrays.stream(ids).map(a -> a.toString()).collect(Collectors.joining(","))));
    var checkerSQL = getSQLChecker(dictionarySQL.GET_CHILD());
    var testRepo = getTestRepository(checkerSQL, checkParam, dictionarySQL);
    testRepo.getChildren(ids);
  }

  @Test
  void testAddVirtualFile() { 
    var factory = IdFactory.createTestFactory();
    var dictionarySQL = DictionarySQL.parseFromResource().get();
    IVirtualFile[] vf = {IVirtualFile.getTestVF(factory), IVirtualFile.getTestVF(factory)};
    var checkParam = getParamChecker(
      Map.of(
        "valuesProperties",
        Arrays.stream(vf).map(a -> String.format(
          "(%d, %s, %d)",
          a.getId().toLong(),
          a.getContent().getFileData().type().isPresent()? a.getContent().getFileData().type().get() : "NONE",
          a.getContent().getCreationTime()
          )
        )
        .collect(Collectors.joining(", ")),
        "valuesRelations",
        Arrays.stream(vf).map(a -> String.format(
          "(%d, %d)",
          a.getId().toLong(),
          a.getContent().getFileData().parentId().isPresent()? a.getContent().getFileData().parentId().get().toLong() : a.getId().toLong()
          )
        )
        .collect(Collectors.joining(", "))
      )
    );
    var checkerSQL = getSQLChecker(dictionarySQL.ADD_VIRTUAL_FILE());
    var testRepo = getTestRepository(checkerSQL, checkParam, dictionarySQL);
    testRepo.addVirtualFile(vf);
  }
}
