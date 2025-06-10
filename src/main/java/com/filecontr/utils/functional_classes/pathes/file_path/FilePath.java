package com.filecontr.utils.functional_classes.pathes.file_path;

import lombok.Data;

@Data
public class FilePath {
  String type;
  String relativePath;
  FilePath innerFilePath;
}
