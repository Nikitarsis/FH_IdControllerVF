package com.filecontr.utils.functional_classes.content;

import com.filecontr.utils.functional_classes.pathes.file_path.FilePath;

public interface IContent {
  Long getCreationTime();
  FilePath getFilePath();
}
