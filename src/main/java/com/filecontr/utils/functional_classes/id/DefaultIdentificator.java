package com.filecontr.utils.functional_classes.id;

class DefaultIdentificator implements IIdentificator {

  private final Long innerId;

  @Override
  public int compareTo(IIdentificator arg0) {
    return innerId.compareTo(arg0.toLong());
  }

  @Override
  public Long toLong() {
    return innerId;
  }

  protected DefaultIdentificator(Long innerId) {
    this.innerId = innerId;
  }
}
