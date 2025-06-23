package com.filecontr.utils.functional_classes.id;

import java.util.function.Function;

import com.filecontr.utils.adapters.logger.AdapterLoggerFactory;
import com.filecontr.utils.adapters.logger.ILogger;

public class IdFactory {
  private final IIdResolver server;
  private final ILogger logger;

  public IdFactory(IIdResolver server, Function<Class<?>, ILogger> loggerProducer) {
    this.server = server;
    logger = loggerProducer.apply(this.getClass());
    logger.info("Created");
  }

  public IIdentificator getNextId() {
    Long id = server.getNextRandomId();
    logger.trace("New Id: " + Long.toHexString(id));
    return new DefaultIdentificator(id);
  }

  public static IdFactory createTestFactory() {
    var testServer = new IIdResolver() {
      private Long id = 0l;
      
      @Override
      public Long getNextRandomId() {
        id++;
        return id - 1;
      }
    };
    return new IdFactory(testServer, AdapterLoggerFactory::getTestLogger);
  }
}
