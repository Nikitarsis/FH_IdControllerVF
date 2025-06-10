package com.filecontr.functional_classes.id;

import com.filecontr.functional_classes.server_data.IServerData;

public interface IIdentificator extends Comparable<IIdentificator> {
  Long getLongValue();
  IServerData getData();
}
