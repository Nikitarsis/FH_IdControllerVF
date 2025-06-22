package com.filecontr.utils.functional_classes.pathes.file_path;

import java.util.Optional;

import com.filecontr.utils.functional_classes.id.IIdentificator;

import lombok.NonNull;

public record FilePath(
  @NonNull Optional<String> type,
  @NonNull String directoryPath,
  @NonNull Optional<IIdentificator> parentId
) {

  public static FilePath createSimpleFilePath(String directoryPath){
    return new FilePath(Optional.empty(), directoryPath, Optional.empty());
  }

  public static FilePath createFilePathWithType(String relativePath, String type){
    return new FilePath(Optional.of(type), relativePath, Optional.empty());
  }

  public static FilePath createTestFilePath() {
    return new FilePath(Optional.of("test"), "./testpath/", Optional.empty());
  }
}