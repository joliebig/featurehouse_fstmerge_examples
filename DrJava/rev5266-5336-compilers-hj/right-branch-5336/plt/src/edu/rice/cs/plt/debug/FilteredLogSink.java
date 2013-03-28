

package edu.rice.cs.plt.debug;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import edu.rice.cs.plt.lambda.LambdaUtil;
import edu.rice.cs.plt.lambda.Predicate;
import edu.rice.cs.plt.text.TextUtil;
import edu.rice.cs.plt.tuple.Option;


public class FilteredLogSink implements LogSink {
  
  private final LogSink _delegate;
  private final Predicate<? super Message> _pred;
  
  public FilteredLogSink(LogSink delegate, Predicate<? super Message> pred) {
    _delegate = delegate;
    _pred = pred;
  }
  
  public void close() throws IOException { _delegate.close(); }

  public void log(StandardMessage m) {
    if (_pred.contains(m)) { _delegate.log(m); }
  }

  public void logEnd(EndMessage m) {
    if (_pred.contains(m)) { _delegate.logEnd(m); }
  }

  public void logError(ErrorMessage m) {
    if (_pred.contains(m)) { _delegate.logError(m); }
  }

  public void logStack(StackMessage m) {
    if (_pred.contains(m)) { _delegate.logStack(m); }
  }

  public void logStart(StartMessage m) {
    if (_pred.contains(m)) { _delegate.logStart(m); }
  }

  
  public static FilteredLogSink byLocationWhiteList(LogSink delegate, final String... prefixes) {
    return new FilteredLogSink(delegate, locationWhiteListPredicate(prefixes));
  }
  
  
  public static FilteredLogSink byLocationBlackList(LogSink delegate, final String... prefixes) {
    return new FilteredLogSink(delegate, locationBlackListPredicate(prefixes));
  }
  
  
  public static FilteredLogSink byLocation(LogSink delegate, final Predicate<? super String> pred) {
    return new FilteredLogSink(delegate, locationPredicate(pred));
  }
  
  
  public static FilteredLogSink byStackDepth(LogSink delegate, int maxDepth) {
    return new FilteredLogSink(delegate, stackDepthPredicate(maxDepth));
  }
  
  
  public static FilteredLogSink byThreadWhiteList(LogSink delegate, Thread... threads) {
    return new FilteredLogSink(delegate, threadWhiteListPredicate(threads));
  }
  
  
  public static FilteredLogSink byThreadWhiteList(LogSink delegate, String... nameParts) {
    return new FilteredLogSink(delegate, threadWhiteListPredicate(nameParts));
  }
  
  
  public static FilteredLogSink byThreadBlackList(LogSink delegate, Thread... threads) {
    return new FilteredLogSink(delegate, threadBlackListPredicate(threads));
  }
  
  
  public static FilteredLogSink byThreadBlackList(LogSink delegate, String... nameParts) {
    return new FilteredLogSink(delegate, threadBlackListPredicate(nameParts));
  }
  
  
  public static FilteredLogSink byThread(LogSink delegate, Predicate<? super ThreadSnapshot> pred) {
    return new FilteredLogSink(delegate, threadPredicate(pred));
  }
  
  
  public static Predicate<Message> locationWhiteListPredicate(final String... prefixes) {
    return locationPredicate(new Predicate<String>() {
      public boolean contains(String s) { return TextUtil.startsWithAny(s, prefixes); }
    });
  }
  
  
  public static Predicate<Message> locationBlackListPredicate(final String... prefixes) {
    return LambdaUtil.negate(locationWhiteListPredicate(prefixes));
  }
  
  
  public static Predicate<Message> locationPredicate(final Predicate<? super String> pred) {
    return new Predicate<Message>() {
      public boolean contains(Message m) {
        Option<StackTraceElement> locOpt = m.caller();
        if (locOpt.isSome()) {
          StackTraceElement loc = locOpt.unwrap();
          return pred.contains(loc.getClassName() + "." + loc.getMethodName());
        }
        else { return pred.contains(""); }
      }
    };
  }
  
  
  public static Predicate<Message> stackDepthPredicate(final int maxDepth) {
    return new Predicate<Message>() {
      public boolean contains(Message m) { return m.thread().getStackTrace().size() <= maxDepth; }
    };
  }
  
  
  public static Predicate<Message> threadWhiteListPredicate(Thread... threads) {
    final Set<Long> ids = new HashSet<Long>();
    for (Thread t : threads) { ids.add(t.getId()); }
    threads = null; 
    return threadPredicate(new Predicate<ThreadSnapshot>() {
      public boolean contains(ThreadSnapshot t) { return ids.contains(t.getId()); }
    });
  }
  
  
  public static Predicate<Message> threadWhiteListPredicate(final String... nameParts) {
    return threadPredicate(new Predicate<ThreadSnapshot>() {
      public boolean contains(ThreadSnapshot t) { return TextUtil.containsAny(t.getName(), nameParts); }
    });
  }
  
  
  public static Predicate<Message> threadBlackListPredicate(Thread... threads) {
    return LambdaUtil.negate(threadWhiteListPredicate(threads));
  }
  
  
  public static Predicate<Message> threadBlackListPredicate(String... nameParts) {
    return LambdaUtil.negate(threadWhiteListPredicate(nameParts));
  }

  
  public static Predicate<Message> threadPredicate(final Predicate<? super ThreadSnapshot> pred) {
    return new Predicate<Message>() {
      public boolean contains(Message m) { return pred.contains(m.thread()); }
    };
  }
  
}
