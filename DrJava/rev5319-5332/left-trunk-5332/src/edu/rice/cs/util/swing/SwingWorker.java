

package edu.rice.cs.util.swing;

import java.awt.EventQueue;


public abstract class SwingWorker {
  private volatile Object _value;  


  
  private static class ThreadVar {
    private volatile Thread _thread;
    ThreadVar(Thread t) { _thread = t; }
    Thread get() { return _thread; }
    void clear() { _thread = null; }
  }

  private volatile ThreadVar _threadVar;

  
  protected Object getValue() { return _value; }

  
  private void setValue(Object x) { _value = x; }

  
  public abstract Object construct();

  
  public void finished() { }

  
  public void interrupt() {
    Thread t = _threadVar.get();
    if (t != null) t.interrupt();
    _threadVar.clear();
  }

  
  public Object get() {
    while (true) {
      Thread t = _threadVar.get();
      if (t == null) return getValue();
      try { t.join(); }
      catch (InterruptedException e) {
        Thread.currentThread().interrupt(); 
        return null;
      }
    }
  }

  
  public SwingWorker() {
    final Runnable doFinished = new Runnable() {
      public void run() { finished(); }
    };

    Runnable doConstruct = new Runnable() {
      public void run() {
        try { setValue(construct()); }
        catch (final RuntimeException e) {
          
          EventQueue.invokeLater(new Runnable() { public void run() { throw e; } });
          throw e;
        }
        catch (final Error e) {
          
          EventQueue.invokeLater(new Runnable() { public void run() { throw e; } });
          throw e;
        }
        finally { _threadVar.clear(); }

        EventQueue.invokeLater(doFinished);
      }
    };

    Thread t = new Thread(doConstruct);
    _threadVar = new ThreadVar(t);
  }

  
  public void start() {
    Thread t = _threadVar.get();
    if (t != null) t.start();
  }
}
