package com.filecontr.utils.functional_classes.id;

import com.filecontr.utils.functional_classes.server_data.IServerData;

public interface IIdentificator extends Comparable<IIdentificator> {
  Long getLongValue();
  IServerData getData();
}
