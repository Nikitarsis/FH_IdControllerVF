package com.filecontr.utils.functional_classes.content;

import com.filecontr.utils.functional_classes.pathes.file_data.FileData;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class DefaultContent implements IContent {
  Long creationTime;
  FileData fileData;
}
