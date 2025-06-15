package com.filecontr.utils.functional_classes.content;

import com.filecontr.utils.functional_classes.pathes.server_path.ServerPath;

public class ContentFactory {
  public static IContent createContent(Long creationTime, ServerPath path) {
    return new DefaultContent(creationTime, path);
  }

  public static IContent createEmptyContent(ServerPath path) {
    return createContent(System.currentTimeMillis(), path);
  }

  public static IContent createTestContent() {
    return createContent(2453l, ServerPath.createTestServerPath());
  }
}
