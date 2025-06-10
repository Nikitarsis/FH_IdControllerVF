package com.filecontr.utils.functional_classes.id;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.filecontr.utils.adapters.logger.AdapterLoggerFactory;
import com.filecontr.utils.adapters.logger.ILogger;

@Service
public class IdFactory {
  private final IIdResolver server;
  private final ILogger logger;

  @Autowired
  public IdFactory(IIdResolver server) {
    this.server = server;
    logger = AdapterLoggerFactory.getLogger(this.getClass());
    logger.info("Created");
  }
  public IIdentificator getNextId() {
    Long id = server.getNextRandomId();
    logger.trace("New Id " + id);
    return new DefaultIdentificator(id, server::getServerDataFromId);
  }

  public static IdFactory getIdFactory(IIdResolver server) {
    return new IdFactory(server);
  }
}
