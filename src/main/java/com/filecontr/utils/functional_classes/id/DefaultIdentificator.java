package com.filecontr.utils.functional_classes.id;

import java.util.function.Function;

import com.filecontr.utils.functional_classes.server_data.IServerData;

class DefaultIdentificator implements IIdentificator {

  private final Long innerId;
  private final Function<Long, IServerData> dataServerCallBack;

  @Override
  public int compareTo(IIdentificator arg0) {
    return innerId.compareTo(arg0.toLong());
  }

  @Override
  public Long toLong() {
    return innerId;
  }

  @Override
  public IServerData getData() {
    return dataServerCallBack.apply(innerId);
  }

  protected DefaultIdentificator(Long innerId, Function<Long, IServerData> callback) {
    this.innerId = innerId;
    this.dataServerCallBack = callback;
  }
}
