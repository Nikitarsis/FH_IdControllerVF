package com.filecontr.utils.functional_classes.pathes.file_data;

import java.util.Optional;

import com.filecontr.utils.functional_classes.id.IIdentificator;

import lombok.NonNull;

public record FileData(
  @NonNull Optional<String> type,
  @NonNull Optional<IIdentificator> parentId
) {

  public static FileData createSimpleFileData(){
    return new FileData(Optional.empty(), Optional.empty());
  }

  public static FileData createFileDataWithType(String type){
    return new FileData(Optional.of(type), Optional.empty());
  }

  public static FileData createTestFileData() {
    return new FileData(Optional.of("test"), Optional.empty());
  }
}