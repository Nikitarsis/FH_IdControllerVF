package com.filecontr.utils.functional_classes.content;

import java.util.HashMap;

public class DefaultContent implements IContent {
  HashMap<Long, String> relations;
  Long creationTime;

  @Override
  public HashMap<Long, String> getRelations() {
    return relations;
  }

  @Override
  public Long getCreationTime() {
    return creationTime;
  }
}
