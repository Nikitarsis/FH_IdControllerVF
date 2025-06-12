package com.filecontr.utils.functional_classes.pathes.file_path;

import java.util.Optional;

import lombok.NonNull;

public record FilePath(
  @NonNull Optional<String> type,
  @NonNull String directoryPath,
  @NonNull Optional<FilePath> innerFilePath
) {

  public static FilePath createSimpleFilePath(String directoryPath){
    return new FilePath(Optional.empty(), directoryPath, Optional.empty());
  }

  public static FilePath createFilePathWithType(String relativePath, String type){
    return new FilePath(Optional.of(type), relativePath, Optional.empty());
  }
}