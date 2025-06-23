package com.filecontr.utils.functional_classes.content;

import com.filecontr.utils.functional_classes.pathes.file_path.FilePath;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
class DefaultContent implements IContent {
  Long creationTime;
  FilePath filePath;
}
