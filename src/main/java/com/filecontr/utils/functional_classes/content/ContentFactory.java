package com.filecontr.utils.functional_classes.content;

import com.filecontr.utils.functional_classes.pathes.file_data.FileData;

public class ContentFactory {
  public static IContent createContent(Long creationTime, FileData path) {
    return new DefaultContent(creationTime, path);
  }

  public static IContent createEmptyContent(FileData path) {
    return createContent(System.currentTimeMillis(), path);
  }

  public static IContent createTestContent() {
    return createContent(2453l, FileData.createTestFileData());
  }
}
