package com.filecontr.utils.functional_classes.server_data;

public class StubServerData implements IServerData {

  @Override
  public String getURL() {
    return "www.testURL.test/a/b";
  }
  
}
