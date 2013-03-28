package edu.rice.cs.plt.debug;

import java.io.IOException;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

import edu.rice.cs.plt.concurrent.CompletionMonitor;
import edu.rice.cs.plt.lambda.LazyRunnable;
import edu.rice.cs.plt.lambda.WrappedException;


public class AsynchronousLogSink implements LogSink {
  
  private final LogSink _delegate;
  private final Runnable _startThread; 
  private final Queue<Message> _queue;
  
  private final CompletionMonitor _emptyNotifier; 
  private final CompletionMonitor _nonemptyNotifier; 
  
  
  public AsynchronousLogSink(LogSink delegate) { this(delegate, true); }
  
  
  public AsynchronousLogSink(LogSink delegate, final boolean flushOnShutdown) {
    _delegate = delegate;
    _startThread = new LazyRunnable(new Runnable() {
      public void run() {
        if (flushOnShutdown) {
          Runtime.getRuntime().addShutdownHook(new Thread() {
            public void run() {
              try { flush(); }
              catch (InterruptedException e) { throw new WrappedException(e); }
            }
          });
        }
        new DequeueThread().start();
      }
    });
    _queue = new ConcurrentLinkedQueue<Message>();
    _emptyNotifier = new CompletionMonitor(true);
    _nonemptyNotifier = new CompletionMonitor(false);
  }
  
  public void close() throws IOException { _delegate.close(); }
  
  
  public void flush() throws InterruptedException {
    _emptyNotifier.ensureSignaled();
  }

  public void log(StandardMessage m) { handle(m); }
  public void logStart(StartMessage m) { handle(m); }
  public void logEnd(EndMessage m) { handle(m); }
  public void logError(ErrorMessage m) { handle(m); }
  public void logStack(StackMessage m) { handle(m); }
  
  private void handle(Message m) {
    boolean wasEmpty = _queue.isEmpty();
    _queue.offer(m);
    if (wasEmpty) {
      synchronized (this) {
        if (!_queue.isEmpty()) { 
          _emptyNotifier.reset();
          _nonemptyNotifier.signal();
        }
      }
      _startThread.run();
    }
  }
  
  private class DequeueThread extends Thread {
    public DequeueThread() { super(AsynchronousLogSink.this.toString()); setDaemon(true); }
    
    public void run() {
      while (true) {
        _nonemptyNotifier.attemptEnsureSignaled();
        while (!_queue.isEmpty()) {
          _queue.remove().send(_delegate);
        }
        synchronized (AsynchronousLogSink.this) {
          if (_queue.isEmpty()) { 
            _nonemptyNotifier.reset();
            _emptyNotifier.signal();
          }
        }
      }
    }
    
  }
  
}
