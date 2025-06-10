package com.filecontr.functional_classes.id;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.filecontr.functional_classes.file_data_server.IFileServer;

@Component
public class IdFactory {
  @Autowired
  private IFileServer server;
  private ILogger logger;

  private IdFactory(){}
  private IdFactory(IFileServer server) {
    this.server = server;
  }
  public IIdentificator getNextId() {
    return new DefaultIdentificator(server.getNextRandomId(), server::getServerDataFromId);
  }

  public static IdFactory getIdFactory(IFileServer server) {
    return new IdFactory(server);
  }
}
