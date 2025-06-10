package com.filecontr.utils.adapters.logger;

public interface ILogger {
  void trace(String msg);
  void info(String msg);
  void debug(String msg);
  void error(String msg);
  void warn(String msg);
}
