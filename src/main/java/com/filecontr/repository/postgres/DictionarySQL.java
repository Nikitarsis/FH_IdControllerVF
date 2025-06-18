package com.filecontr.repository.postgres;

import java.util.Arrays;
import java.util.Scanner;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public record DictionarySQL(
  String GET_VIRTUAL_FILE
) {
  public static DictionarySQL parseFromResource() {
    String text;
    String path = "/databases/requests";
    try(var scanner = new Scanner(DictionarySQL.class.getResourceAsStream(path)).useDelimiter(Pattern.compile("\A"))) {
      text = scanner.hasNext()? scanner.next() : "";
    }
    var map = Arrays.stream(text.split("\n"))
      .map(a -> a.split("--"))
      .collect(Collectors.toMap(a -> a[0], a -> a[1]));
    return new DictionarySQL(
      map.get("GET_VIRTUAL_FILE")
    );
  }
}