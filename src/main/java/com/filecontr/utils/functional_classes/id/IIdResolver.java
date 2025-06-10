package com.filecontr.utils.functional_classes.id;

import com.filecontr.utils.functional_classes.server_data.IServerData;

public interface IIdResolver {
  IServerData getServerDataFromId(Long id);
  Long getNextRandomId();
}
