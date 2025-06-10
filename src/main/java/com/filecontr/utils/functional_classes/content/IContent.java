package com.filecontr.utils.functional_classes.content;

import java.util.HashMap;

public interface IContent {
  HashMap<Long, String> getRelations();
  Long getCreationTime();
}
