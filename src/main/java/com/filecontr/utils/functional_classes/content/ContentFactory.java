package com.filecontr.utils.functional_classes.content;

import com.filecontr.utils.functional_classes.pathes.file_path.FilePath;

public class ContentFactory {
  public static IContent createContent(Long creationTime, FilePath path) {
    return new DefaultContent(creationTime, path);
  }

  public static IContent createEmptyContent(FilePath path) {
    return createContent(System.currentTimeMillis(), path);
  }

  public static IContent createTestContent() {
    return createContent(2453l, FilePath.createTestFilePath());
  }
}
