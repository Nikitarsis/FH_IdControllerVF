package com.filecontr.utils.functional_classes.pathes.server_path;

import com.filecontr.utils.functional_classes.pathes.file_path.FilePath;

import lombok.Data;

@Data
public class ServerPath {
  String serverURL;
  FilePath file;
}
