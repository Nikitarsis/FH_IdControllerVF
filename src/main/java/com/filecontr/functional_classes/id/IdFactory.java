package com.filecontr.functional_classes.id;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.filecontr.adapters.logger.ILogger;
import com.filecontr.functional_classes.file_data_server.IFileServer;

@Component
public class IdFactory {
  @Autowired
  private IFileServer server;
  @Autowired
  private ILogger logger;

  private IdFactory(){}
  private IdFactory(IFileServer server) {
    this.server = server;
  }
  public IIdentificator getNextId() {
    Long id = server.getNextRandomId();
    logger.trace("New Id " + id);
    return new DefaultIdentificator(id, server::getServerDataFromId);
  }

  public static IdFactory getIdFactory(IFileServer server) {
    return new IdFactory(server);
  }
}
