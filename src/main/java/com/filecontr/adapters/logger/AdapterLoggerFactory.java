package com.filecontr.adapters.logger;

public class AdapterLoggerFactory {
  public static ILogger getLogger(Class<?> logClass) {
    return new SimpleLogger(logClass);
  }
}
