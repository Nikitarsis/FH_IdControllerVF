package com.filecontr.utils.functional_classes.content;

import com.filecontr.utils.functional_classes.pathes.file_path.FilePath;
import com.filecontr.utils.functional_classes.pathes.server_path.ServerPath;

public interface IContent {
  Long getCreationTime();
  FilePath getFilePath();
  ServerPath getServerPath();
}
