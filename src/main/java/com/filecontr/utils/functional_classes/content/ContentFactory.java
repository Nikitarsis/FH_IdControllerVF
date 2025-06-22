package com.filecontr.utils.functional_classes.content;

import com.filecontr.utils.functional_classes.pathes.file_path.FilePath;
import com.filecontr.utils.functional_classes.pathes.server_path.ServerPath;

public class ContentFactory {
  public static IContent createContent(Long creationTime, FilePath path, ServerPath serverPath) {
    return new DefaultContent(creationTime, path, serverPath);
  }

  public static IContent createEmptyContent(FilePath path, ServerPath serverPath) {
    return createContent(System.currentTimeMillis(), path, serverPath);
  }

  public static IContent createTestContent() {
    return createContent(2453l, FilePath.createTestFilePath(), ServerPath.createTestServerPath());
  }
}
