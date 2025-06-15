package com.filecontr.utils.adapters.logger;

public class AdapterLoggerFactory {
  public static ILogger getLogger(Class<?> logClass) {
    return new SimpleLogger(logClass);
  }

  public static ILogger getTestLogger(Class<?> logClass) {
    return new SimpleLogger(logClass);
  }
}
