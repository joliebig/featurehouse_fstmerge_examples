

package edu.rice.cs.util;

import java.io.*;

import java.util.Date;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.TimeZone;


public class Log {
  public static final boolean ENABLE_ALL = false;
  
  
  protected volatile boolean _isEnabled;
  
  
  protected volatile String _name;
  
  
  protected volatile File _file;
  
  
  protected volatile PrintWriter _writer;
  
  public final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("d MMM yyyy H:mm:ss z");
  
  
  public Log(String name, boolean isEnabled) { this(new File(name), isEnabled); }
  
  public Log(File f, boolean isEnabled) {
    _file = f;
    _name = f.getName();
    _isEnabled = isEnabled;
    DATE_FORMAT.setTimeZone(TimeZone.getTimeZone("GMT"));
    DATE_FORMAT.setLenient(false);
    _init();
  }
  
  
  protected void _init() {
    if (_writer == null) {
      if (_isEnabled || ENABLE_ALL) {
        try {
          FileWriter w = new FileWriter(_file.getAbsolutePath(), true);
          _writer = new PrintWriter(w);
          log("Log '" + _name + "' opened: " + DATE_FORMAT.format(new Date()));
        }
        catch (IOException ioe) {
          throw new RuntimeException("Could not create log: " + ioe);
        }
      }
    }
  }
  
  
  public void setEnabled(boolean isEnabled) { _isEnabled = isEnabled; }
  
  
  public boolean isEnabled() { return (_isEnabled || ENABLE_ALL); }
  
  
  public synchronized void log(String message) {
    if (isEnabled()) {
      if (_writer == null) {
        _init();
      }
      _writer.println(DATE_FORMAT.format(new Date()) + ": " + message);
      _writer.flush();
    }
  }
  
  
  public static String traceToString(StackTraceElement[] trace) {
    final StringBuilder traceImage = new StringBuilder();
    for (StackTraceElement e: trace) traceImage.append("\n\tat " + e.toString());
    return traceImage.toString();
  }
  
  
  public synchronized void log(String s, StackTraceElement[] trace) {
    if (isEnabled()) log(s + traceToString(trace));
  }
  
  
  public synchronized void log(String s, Throwable t) {
    if (isEnabled()) {
      StringWriter sw = new StringWriter();
      PrintWriter pw = new PrintWriter(sw);
      t.printStackTrace(pw);
      log(s + "\n" + sw.toString());
    }
  }
  
  
  public void close() {
    _writer.close();
    _writer = null;
  }
  
  
  public synchronized Date parse(String s) {
    int pos = s.indexOf("GMT: ");
    if (pos == -1) { return null; }
    try {
      return DATE_FORMAT.parse(s.substring(0,pos+3));
    }
    catch(ParseException pe) { return null; }
  }
}
