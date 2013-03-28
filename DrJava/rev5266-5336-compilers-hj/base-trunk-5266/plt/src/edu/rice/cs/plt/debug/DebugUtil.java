

package edu.rice.cs.plt.debug;

import java.io.File;
import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import edu.rice.cs.plt.iter.IterUtil;
import edu.rice.cs.plt.iter.SizedIterable;
import edu.rice.cs.plt.lambda.*;
import edu.rice.cs.plt.text.Bracket;
import edu.rice.cs.plt.text.TextUtil;
import edu.rice.cs.plt.tuple.Pair;
import edu.rice.cs.plt.reflect.ReflectUtil;
import edu.rice.cs.plt.reflect.ReflectException;


public final class DebugUtil {
  
  
  private DebugUtil() {}
  
  
  public static volatile Log debug;

  
  public static volatile Log error;
  
  static { initializeLogs(); }
  
  
  public static void initializeLogs() {
    String debugProp = System.getProperty("plt.debug.log");
    debug = (debugProp == null) ? VoidLog.INSTANCE : makeLog(debugProp, "Debug");
    String errorProp = System.getProperty("plt.error.log");
    error = (errorProp == null) ? VoidLog.INSTANCE : makeLog(errorProp, "Error");
  }
  
  
  public static Log makeLog(String descriptor, String defaultName) {
    LogSink sink = makeLogSink(descriptor, defaultName);
    if (sink == null) { return VoidLog.INSTANCE; }
    else { return new StandardLog(sink); }
  }
  
  
  public static LogSink makeLogSink(String descriptor, String defaultName) {
    String[] split = TextUtil.split(descriptor, ",", Bracket.PARENTHESES, Bracket.APOSTROPHES).array();
    List<LogSink> sinks = new ArrayList<LogSink>(split.length);
    for (String s : split) {
      LogSink sink = makeFilteredLogSink(s.trim(), defaultName);
      if (sink != null) { sinks.add(sink); }
    }
    if (sinks.isEmpty()) { return null; }
    else if (sinks.size() == 1) { return sinks.get(0); }
    else { return new SplitLogSink(sinks); }
  }
  
  private static LogSink makeFilteredLogSink(String descriptor, String defaultName) {
    TextUtil.SplitString split = TextUtil.split(descriptor, "\\+|-", Bracket.PARENTHESES, Bracket.APOSTROPHES);
    String desc;
    SizedIterable<Pair<String, String>> filters;
    if (split.splits().isEmpty()) { desc = descriptor; filters = IterUtil.empty(); }
    else {
      desc = split.splits().get(0).trim();
      Iterable<String> filterText = IterUtil.compose(IterUtil.skipFirst(split.splits()), split.rest());
      filters = IterUtil.zip(split.delimiters(), filterText);
    }
    
    LogSink result;
    if (desc.startsWith("(")) {
      if (desc.endsWith(")")) { result = makeLogSink(desc.substring(1, desc.length()-1), defaultName); }
      else { return null; } 
    }
    else if (desc.startsWith("~")) {
      result = new AsynchronousLogSink(makeAtomicLogSink(desc.substring(1), defaultName));
    }
    else {
      result = makeAtomicLogSink(desc, defaultName);
    }
    
    if (!filters.isEmpty()) {
      List<String> whiteListLocs = new ArrayList<String>();
      List<String> blackListLocs = new ArrayList<String>();
      List<String> whiteListThreads = new ArrayList<String>();
      List<String> blackListThreads = new ArrayList<String>();
      for (Pair<String, String> p : filters) {
        String text = p.second().trim();
        boolean thread = text.startsWith("'");
        if (thread) {
          if (text.endsWith("'")) { text = text.substring(1, text.length()-1); }
          else { return null; } 
        }
        if (p.first().equals("+")) {
          (thread ? whiteListThreads : whiteListLocs).add(text);
        }
        else if (p.first().equals("-")) {
          (thread ? blackListThreads : blackListLocs).add(text);
        }
        else { throw new RuntimeException("Bad delimiter from TextUtil.split: " + p.first()); }
      }
      if (!blackListLocs.isEmpty()) {
        result = FilteredLogSink.byLocationBlackList(result, IterUtil.toArray(blackListLocs, String.class));
      }
      if (!blackListThreads.isEmpty()) {
        result = FilteredLogSink.byThreadBlackList(result, IterUtil.toArray(blackListThreads, String.class));
      }
      if (!whiteListLocs.isEmpty()) {
        result = FilteredLogSink.byLocationWhiteList(result, IterUtil.toArray(whiteListLocs, String.class));
      }
      if (!whiteListThreads.isEmpty()) {
        result = FilteredLogSink.byThreadWhiteList(result, IterUtil.toArray(whiteListThreads, String.class));
      }
    }
    return result;
  }
  
  private static LogSink makeAtomicLogSink(String descriptor, String defaultName) {
    String[] split = TextUtil.split(descriptor, ":", 2, Bracket.PARENTHESES, Bracket.APOSTROPHES).array();
    String name = split[0].trim();
    String arg = (split.length > 1) ? split[1].trim() : ""; 
    if (arg.length() >= 2 && arg.startsWith("'") && arg.endsWith("'")) { arg = arg.substring(1, arg.length()-1); }
    LogSink result = null;
    String factoryName = System.getProperty("plt.log.factory");
    if (factoryName != null) {
      int dot = factoryName.lastIndexOf('.');
      if (dot >= 0) {
        String className = factoryName.substring(0, dot);
        String methodName = factoryName.substring(dot+1);
        try {
          result = (LogSink) ReflectUtil.invokeStaticMethod(className, methodName, name, arg, defaultName);
        }
        catch (ReflectException e) {
          System.err.println("Unable to invoke plt.log.factory: " + e.getCause());
        }
        catch (ClassCastException e) {
          System.err.println("Unable to invoke plt.log.factory: " + e);
        }
      }
    }
    if (result == null) {
      try {
        if (name.equals("System.out") || name.equals("stdout")) {
          if (arg.equals("")) { result = new SystemOutLogSink(); }
          else { result = new SystemOutLogSink(arg); }
        }
        else if (name.equals("System.err") || name.equals("stderr")) {
          if (arg.equals("")) { result = new SystemErrLogSink(); }
          else { result = new SystemErrLogSink(arg); }
        }
        else if (name.equals("file")) {
          if (arg.equals("")) { arg = defaultName.toLowerCase().replace(' ', '-') + "-log.txt"; }
          String workingDir = System.getProperty("plt.log.working.dir");
          if (workingDir == null) { result = new FileLogSink(arg); }
          else { result = new FileLogSink(new File(workingDir, arg)); }
        }
        else if (name.equals("assert")) {
          result = AssertEmptyLogSink.INSTANCE;
        }
        else if (name.equals("popup")) {
          if (arg.equals("")) { arg = defaultName; }
          result = new PopupLogSink(arg);
        }
        else if (name.equals("tree")) {
          if (arg.equals("")) { arg = defaultName; }
          result = remoteTreeLogSink(arg);
        }
        
      }
      catch (Exception e) {  }
    }
    return result;
  }
  
  public static Log voidLog() { return VoidLog.INSTANCE; }
  public static Log assertEmptyLog() { return new StandardLog(AssertEmptyLogSink.INSTANCE); }
  public static Log systemOutLog() { return new StandardLog(new SystemOutLogSink()); }
  public static Log systemOutLog(String charsetName) throws UnsupportedEncodingException {
    return new StandardLog(new SystemOutLogSink(charsetName));
  }
  public static Log systemErrLog() { return new StandardLog(new SystemErrLogSink()); }
  public static Log systemErrLog(String charsetName) throws UnsupportedEncodingException {
    return new StandardLog(new SystemErrLogSink(charsetName));
  }
  public static Log fileLog(String filename) { return new StandardLog(new FileLogSink(filename)); }
  public static Log fileLog(File f) { return new StandardLog(new FileLogSink(f)); }
  public static Log popupLog(String name) { return new StandardLog(new PopupLogSink(name)); }
  public static Log remoteTreeLog(String name) { return new StandardLog(remoteTreeLogSink(name)); }

   
  public static LogSink remoteTreeLogSink(String name) {
    return new RMILogSink(TreeLogSink.factory(name, true), false);
  }
  
  
  public static boolean check(boolean assertion) {
    assert assertion;
    return assertion;
  }
  
  
  public static StackTraceElement getCaller() {
    
    
    
    try { return new Throwable().getStackTrace()[2]; }
    catch (ArrayIndexOutOfBoundsException e) {
      throw new IllegalStateException("Stack trace information for caller is not available");
    }
  }
  

   
  private static final Thunk<Timer> LOG_TIMER = new LazyThunk<Timer>(new Thunk<Timer>() {
    public Timer value() {
      return new Timer("Delayed Log Timer", true);
    }
  });
  
  
  public static void logThreadStatus(Log log, long... delays) {
    logThreadStatus(log, Thread.currentThread(), delays);
  }
  
  
  public static void logThreadStatus(final Log log, final Thread thread, long... delays) {
    
    class LogTask extends TimerTask {
      public void run() {
        log.logValues(new String[]{ "thread", "state", "stack" },
                      thread, thread.getState(), thread.getStackTrace());
      }
    }
    if (delays.length == 0) { new LogTask().run(); }
    for (final long delay : delays) {
      LOG_TIMER.value().schedule(new LogTask(), delay);
    }
  }
  
  
  public static Runnable logExceptions(Log l, Runnable r) {
    return new LogExceptionRunnable(l, r);
  }
  
  private static final class LogExceptionRunnable implements Runnable, Serializable {
    private final Log _log;
    private final Runnable _r;
    public LogExceptionRunnable(Log log, Runnable r) { _log = log; _r = r; }
    public void run() {
      try { _r.run(); }
      catch (RuntimeException e) { _log.log(e); }
    }
  }
  
  
  public static Runnable logThrowables(Log l, Runnable r) {
    return new LogThrowableRunnable(l, r);
  }
  
  private static final class LogThrowableRunnable implements Runnable, Serializable {
    private final Log _log;
    private final Runnable _r;
    public LogThrowableRunnable(Log log, Runnable r) { _log = log; _r = r; }
    public void run() {
      try { _r.run(); }
      catch (Throwable t) { _log.log(t); }
    }
  }
  
  
  public static <T> Runnable1<T> logExceptions(Log l, Runnable1<? super T> r) {
    return new LogExceptionRunnable1<T>(l, r);
  }
  
  private static final class LogExceptionRunnable1<T> implements Runnable1<T>, Serializable {
    private final Log _log;
    private final Runnable1<? super T> _r;
    public LogExceptionRunnable1(Log log, Runnable1<? super T> r) { _log = log; _r = r; }
    public void run(T arg) {
      try { _r.run(arg); }
      catch (RuntimeException e) { _log.log(e); }
    }
  }
  
  
  public static <T> Runnable1<T> logThrowables(Log l, Runnable1<? super T> r) {
    return new LogThrowableRunnable1<T>(l, r);
  }
  
  private static final class LogThrowableRunnable1<T> implements Runnable1<T>, Serializable {
    private final Log _log;
    private final Runnable1<? super T> _r;
    public LogThrowableRunnable1(Log log, Runnable1<? super T> r) { _log = log; _r = r; }
    public void run(T arg) {
      try { _r.run(arg); }
      catch (Throwable t) { _log.log(t); }
    }
  }
  
  
  public static <T1, T2> Runnable2<T1, T2> logExceptions(Log l, Runnable2<? super T1, ? super T2> r) {
    return new LogExceptionRunnable2<T1, T2>(l, r);
  }
  
  private static final class LogExceptionRunnable2<T1, T2> implements Runnable2<T1, T2>, Serializable {
    private final Log _log;
    private final Runnable2<? super T1, ? super T2> _r;
    public LogExceptionRunnable2(Log log, Runnable2<? super T1, ? super T2> r) { _log = log; _r = r; }
    public void run(T1 arg1, T2 arg2) {
      try { _r.run(arg1, arg2); }
      catch (RuntimeException e) { _log.log(e); }
    }
  }
  
  
  public static <T1, T2> Runnable2<T1, T2> logThrowables(Log l, Runnable2<? super T1, ? super T2> r) {
    return new LogThrowableRunnable2<T1, T2>(l, r);
  }
  
  private static final class LogThrowableRunnable2<T1, T2> implements Runnable2<T1, T2>, Serializable {
    private final Log _log;
    private final Runnable2<? super T1, ? super T2> _r;
    public LogThrowableRunnable2(Log log, Runnable2<? super T1, ? super T2> r) { _log = log; _r = r; }
    public void run(T1 arg1, T2 arg2) {
      try { _r.run(arg1, arg2); }
      catch (Throwable t) { _log.log(t); }
    }
  }
  
  
  public static <T1, T2, T3>
  Runnable3<T1, T2, T3> logExceptions(Log l, Runnable3<? super T1, ? super T2, ? super T3> r) {
    return new LogExceptionRunnable3<T1, T2, T3>(l, r);
  }
  
  private static final class LogExceptionRunnable3<T1, T2, T3> implements Runnable3<T1, T2, T3>, Serializable {
    private final Log _log;
    private final Runnable3<? super T1, ? super T2, ? super T3> _r;
    public LogExceptionRunnable3(Log log, Runnable3<? super T1, ? super T2, ? super T3> r) { _log = log; _r = r; }
    public void run(T1 arg1, T2 arg2, T3 arg3) {
      try { _r.run(arg1, arg2, arg3); }
      catch (RuntimeException e) { _log.log(e); }
    }
  }
  
  
  public static <T1, T2, T3>
  Runnable3<T1, T2, T3> logThrowables(Log l, Runnable3<? super T1, ? super T2, ? super T3> r) {
    return new LogThrowableRunnable3<T1, T2, T3>(l, r);
  }
  
  private static final class LogThrowableRunnable3<T1, T2, T3> implements Runnable3<T1, T2, T3>, Serializable {
    private final Log _log;
    private final Runnable3<? super T1, ? super T2, ? super T3> _r;
    public LogThrowableRunnable3(Log log, Runnable3<? super T1, ? super T2, ? super T3> r) { _log = log; _r = r; }
    public void run(T1 arg1, T2 arg2, T3 arg3) {
      try { _r.run(arg1, arg2, arg3); }
      catch (Throwable t) { _log.log(t); }
    }
  }
  
  
  public static <T1, T2, T3, T4>
  Runnable4<T1, T2, T3, T4> logExceptions(Log l, Runnable4<? super T1, ? super T2, ? super T3, ? super T4> r) {
    return new LogExceptionRunnable4<T1, T2, T3, T4>(l, r);
  }
  
  private static final class LogExceptionRunnable4<T1, T2, T3, T4> implements Runnable4<T1, T2, T3, T4>, Serializable {
    private final Log _log;
    private final Runnable4<? super T1, ? super T2, ? super T3, ? super T4> _r;
    public LogExceptionRunnable4(Log log, Runnable4<? super T1, ? super T2, ? super T3, ? super T4> r) {
      _log = log; _r = r;
    }
    public void run(T1 arg1, T2 arg2, T3 arg3, T4 arg4) {
      try { _r.run(arg1, arg2, arg3, arg4); }
      catch (RuntimeException e) { _log.log(e); }
    }
  }
  
  
  public static <T1, T2, T3, T4>
  Runnable4<T1, T2, T3, T4> logThrowables(Log l, Runnable4<? super T1, ? super T2, ? super T3, ? super T4> r) {
    return new LogThrowableRunnable4<T1, T2, T3, T4>(l, r);
  }
  
  private static final class LogThrowableRunnable4<T1, T2, T3, T4> implements Runnable4<T1, T2, T3, T4>, Serializable {
    private final Log _log;
    private final Runnable4<? super T1, ? super T2, ? super T3, ? super T4> _r;
    public LogThrowableRunnable4(Log log, Runnable4<? super T1, ? super T2, ? super T3, ? super T4> r) {
      _log = log; _r = r;
    }
    public void run(T1 arg1, T2 arg2, T3 arg3, T4 arg4) {
      try { _r.run(arg1, arg2, arg3, arg4); }
      catch (Throwable t) { _log.log(t); }
    }
  }
  
}
