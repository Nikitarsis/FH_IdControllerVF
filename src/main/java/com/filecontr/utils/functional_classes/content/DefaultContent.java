package com.filecontr.utils.functional_classes.content;

import java.util.HashMap;

import com.filecontr.utils.functional_classes.pathes.server_path.ServerPath;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class DefaultContent implements IContent {
  HashMap<Long, String> relations;
  Long creationTime;
  ServerPath path;

  @Override
  public HashMap<Long, String> getRelations() {
    return relations;
  }

  @Override
  public Long getCreationTime() {
    return creationTime;
  }

  @Override
  public ServerPath getServerPath() {
    return path;
  }
}
