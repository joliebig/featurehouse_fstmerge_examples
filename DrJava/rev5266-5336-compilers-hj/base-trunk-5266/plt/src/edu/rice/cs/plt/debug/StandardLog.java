

package edu.rice.cs.plt.debug;

import edu.rice.cs.plt.debug.LogSink.*;


public class StandardLog implements Log {
  
  private static final String[] EMPTY = new String[0];
  
  private final LogSink _sink;
  
  public StandardLog(LogSink sink) { _sink = sink; }

  public void log() {
    _sink.log(new StandardMessage(new ThreadSnapshot(), EMPTY, EMPTY));
  }

  public void log(String message) {
    _sink.log(new StandardMessage(new ThreadSnapshot(), message, EMPTY, EMPTY));
  }

  public void log(Throwable t) {
    _sink.logError(new ErrorMessage(new ThreadSnapshot(), t));
  }

  public void log(String message, Throwable t) {
    _sink.logError(new ErrorMessage(new ThreadSnapshot(), message, t));
  }

  public void logEnd() {
    _sink.logEnd(new EndMessage(new ThreadSnapshot(), EMPTY, EMPTY));
  }

  public void logEnd(String message) {
    _sink.logEnd(new EndMessage(new ThreadSnapshot(), message, EMPTY, EMPTY));
  }

  public void logEnd(String name, Object value) {
    _sink.logEnd(new EndMessage(new ThreadSnapshot(), new String[]{ name }, new Object[]{ value }));
  }

  public void logEnd(String message, String name, Object value) {
    _sink.logEnd(new EndMessage(new ThreadSnapshot(), message, new String[]{ name }, new Object[]{ value }));
  }

  public void logEnd(String[] names, Object... values) {
    _sink.logEnd(new EndMessage(new ThreadSnapshot(), names, values));
  }

  public void logEnd(String message, String[] names, Object... values) {
    _sink.logEnd(new EndMessage(new ThreadSnapshot(), message, names, values));
  }

  public void logStack() {
    _sink.logStack(new StackMessage(new ThreadSnapshot()));
  }

  public void logStack(String message) {
    _sink.logStack(new StackMessage(new ThreadSnapshot(), message));
  }

  public void logStart() {
    _sink.logStart(new StartMessage(new ThreadSnapshot(), EMPTY, EMPTY));
  }

  public void logStart(String message) {
    _sink.logStart(new StartMessage(new ThreadSnapshot(), message, EMPTY, EMPTY));
  }

  public void logStart(String name, Object value) {
    _sink.logStart(new StartMessage(new ThreadSnapshot(), new String[]{ name }, new Object[]{ value }));
  }

  public void logStart(String message, String name, Object value) {
    _sink.logStart(new StartMessage(new ThreadSnapshot(), message, new String[]{ name }, new Object[]{ value }));
  }

  public void logStart(String[] names, Object... values) {
    _sink.logStart(new StartMessage(new ThreadSnapshot(), names, values));
  }

  public void logStart(String message, String[] names, Object... values) {
    _sink.logStart(new StartMessage(new ThreadSnapshot(), message, names, values));
  }

  public void logValue(String name, Object value) {
    _sink.log(new StandardMessage(new ThreadSnapshot(), new String[]{ name }, new Object[]{ value }));
  }

  public void logValue(String message, String name, Object value) {
    _sink.log(new StandardMessage(new ThreadSnapshot(), message, new String[]{ name }, new Object[]{ value }));
  }

  public void logValues(String[] names, Object... values) {
    _sink.log(new StandardMessage(new ThreadSnapshot(), names, values));
  }

  public void logValues(String message, String[] names, Object... values) {
    _sink.log(new StandardMessage(new ThreadSnapshot(), message, names, values));
  }

}
