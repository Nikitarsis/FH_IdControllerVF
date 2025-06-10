package com.filecontr.utils.functional_classes.pathes.file_path;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class FilePath {
  String type;
  String relativePath;
  FilePath innerFilePath;
}