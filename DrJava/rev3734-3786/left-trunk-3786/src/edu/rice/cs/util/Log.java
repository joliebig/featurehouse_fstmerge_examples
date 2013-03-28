

package edu.rice.cs.util;

import java.io.*;

import java.util.Date;


public class Log {
  public static final boolean ENABLE_ALL = false;

  
  protected boolean _isEnabled;

  
  protected String _name;

  
  protected PrintWriter _writer;

  
  public Log(String name, boolean isEnabled) {
    _name = name;
    _isEnabled = isEnabled;
    _init();
  }

  
  protected void _init() {
    if (_writer == null) {
      if (_isEnabled || ENABLE_ALL) {
        try {
          File f = new File(_name);
          FileWriter w = new FileWriter(f.getAbsolutePath(), true);
          _writer = new PrintWriter(w);

          logTime("Log '" + _name + "' opened: " + (new Date()));
        }
        catch (IOException ioe) {
          throw new RuntimeException("Could not create log: " + ioe);
        }
      }
    }
  }

  
  public void setEnabled(boolean isEnabled) {
    _isEnabled = isEnabled;
  }

  
  public boolean isEnabled() {
    return (_isEnabled || ENABLE_ALL);
  }

  
  public synchronized void log(String message) {
    if (isEnabled()) {
      if (_writer == null) {
        _init();
      }
      _writer.println(message);
      _writer.flush();
    }
  }

  
  public synchronized void logTime(String message) {
    if (isEnabled()) {
      long t = System.currentTimeMillis();
      log(t + ": " + message);
    }
  }

  
  public synchronized void logTime(String s, Throwable t) {
    if (isEnabled()) {
      StringWriter sw = new StringWriter();
      PrintWriter pw = new PrintWriter(sw);
      t.printStackTrace(pw);
      logTime(s + "\n" + sw.toString());
    }
  }
}