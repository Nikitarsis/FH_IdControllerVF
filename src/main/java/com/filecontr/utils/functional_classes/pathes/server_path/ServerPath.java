package com.filecontr.utils.functional_classes.pathes.server_path;

import com.filecontr.utils.functional_classes.pathes.file_path.FilePath;

import lombok.NonNull;

public record ServerPath(
  @NonNull String serverURL,
  @NonNull FilePath file
) {

  public static ServerPath createSimpleServerPath(String URL, String filePath) {
    return new ServerPath(URL, FilePath.createSimpleFilePath(filePath));
  }

  public static ServerPath createTestServerPath() {
    return new ServerPath("www.TestUrl.test/a/b", FilePath.createTestFilePath());
  }
}
