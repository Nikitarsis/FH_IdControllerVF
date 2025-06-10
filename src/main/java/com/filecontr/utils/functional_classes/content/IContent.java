package com.filecontr.utils.functional_classes.content;

import java.util.HashMap;

import com.filecontr.utils.functional_classes.server_data.IServerData;

public interface IContent {
  HashMap<Long, String> getRelations();
  Long getCreationTime();
  IServerData getServerData();
}
