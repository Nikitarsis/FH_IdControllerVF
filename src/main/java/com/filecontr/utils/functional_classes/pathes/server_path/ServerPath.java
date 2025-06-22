package com.filecontr.utils.functional_classes.pathes.server_path;

import lombok.NonNull;

public record ServerPath(
  @NonNull String serverURL
) {

  public static ServerPath createSimpleServerPath(String URL, String filePath) {
    return new ServerPath(URL);
  }

  public static ServerPath createTestServerPath() {
    return new ServerPath("www.TestUrl.test/a/b");
  }
}
