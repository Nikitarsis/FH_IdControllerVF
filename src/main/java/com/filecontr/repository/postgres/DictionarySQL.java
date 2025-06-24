package com.filecontr.repository.postgres;

import java.util.Map;
import java.util.Optional;
import java.util.Scanner;
import java.util.function.Function;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public record DictionarySQL(
  String GET_VIRTUAL_FILE,
  String GET_RELATIONS,
  String ADD_VIRTUAL_FILE,
  String UPDATE_VIRTUAL_FILE,
  String DELETE_VIRTUAL_FILE,
  String GET_PARENT,
  String GET_CHILD
) {

  protected static DictionarySQL createFromMap(Function<String, String> dictionaryMapper) {
    return new DictionarySQL(
      dictionaryMapper.apply("GET_VIRTUAL_FILE"),
      dictionaryMapper.apply("GET_RELATIONS"),
      dictionaryMapper.apply("ADD_VIRTUAL_FILE"),
      dictionaryMapper.apply("UPDATE_VIRTUAL_FILE"),
      dictionaryMapper.apply("DELETE_VIRTUAL_FILE"),
      dictionaryMapper.apply("GET_PARENT"),
      dictionaryMapper.apply("GET_CHILD")
    );
  }

  public static Optional<DictionarySQL> parseFromResource() {
    final String path = "/databases/requests";
    try(var scanner = new Scanner(DictionarySQL.class.getResourceAsStream(path)).useDelimiter(Pattern.compile("\n"))) {
      final Map<String, String> map = scanner.tokens().map(a -> a.split("--"))
      .collect(Collectors.toMap(a -> a[0], a -> a[1]));
      Function<String, String> func = (String key) -> {
        if (map.containsKey(key)) {
          return map.get(key);
        }
        throw new RuntimeException(String.format("No such SQL Request name: %s", key));
      };
      return Optional.of(createFromMap(func));
    }
  }
}