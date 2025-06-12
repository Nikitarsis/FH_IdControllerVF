package com.filecontr.utils.functional_classes.content;

import java.util.HashMap;

import com.filecontr.utils.functional_classes.pathes.server_path.ServerPath;

public class ContentFactory {
  public static IContent createContent(HashMap<Long, String> relations, Long creationTime, ServerPath path) {
    return new DefaultContent(relations, creationTime, path);
  }

  public static IContent createEmptyContent(ServerPath path) {
    return createContent(new HashMap<Long, String>(), System.currentTimeMillis(), path);
  }
}
