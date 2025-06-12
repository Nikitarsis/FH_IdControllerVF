package com.filecontr.utils.functional_classes.content;

import java.util.HashMap;

import com.filecontr.utils.functional_classes.pathes.server_path.ServerPath;

public interface IContent {
  HashMap<Long, String> getRelations();
  Long getCreationTime();
  ServerPath getServerPath();
}
