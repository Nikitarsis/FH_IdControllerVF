package com.filecontr.utils.adapters.logger;

import java.text.SimpleDateFormat;
import java.util.Calendar;

class SimpleLogger implements ILogger {

  String classMessage;
  private static SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd_HHmmss");

  private String decorateMessage(String type, String msg) {  
    String timeStamp = dateFormat.format(Calendar.getInstance().getTime());
    return String.format("%s%s - %s: %s", type, classMessage, timeStamp, msg);
  }

  @Override
  public void trace(String msg) {
    System.out.println(decorateMessage("TRACE: ", msg));
  }

  @Override
  public void info(String msg) {
    System.out.println(decorateMessage("INFO: ", msg));
  }

  @Override
  public void debug(String msg) {
    System.out.println(decorateMessage("DEBUG: ", msg));
  }

  @Override
  public void error(String msg) {
    System.err.println(decorateMessage("ERROR: ", msg));
  }

  @Override
  public void warn(String msg) {
    System.err.println(decorateMessage("WARN: ", msg));
  }

  SimpleLogger(Class<?> logClass) {
    this.classMessage = logClass.getSimpleName();
  }
  
}
