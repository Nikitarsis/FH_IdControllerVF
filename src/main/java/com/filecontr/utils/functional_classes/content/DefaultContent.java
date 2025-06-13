package com.filecontr.utils.functional_classes.content;

import com.filecontr.utils.functional_classes.pathes.server_path.ServerPath;

import lombok.AllArgsConstructor;

@AllArgsConstructor
class DefaultContent implements IContent {
  Long creationTime;
  ServerPath path;

  @Override
  public Long getCreationTime() {
    return creationTime;
  }

  @Override
  public ServerPath getServerPath() {
    return path;
  }
}
