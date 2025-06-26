package com.filecontr.service.server_data;

import com.filecontr.utils.functional_classes.id.IIdentificator;

public interface IServerData {
  IIdentificator getNextId();
  IServerId getServerId();
}
