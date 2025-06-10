package com.filecontr.utils.functional_classes.file_data_server;

import com.filecontr.utils.functional_classes.server_data.IServerData;

public interface IFileServer {
  IServerData getServerDataFromId(Long id);
  Long getNextRandomId();
  IServerData serverInfoById(Long id);
}
