

package edu.rice.cs.util.swing;

import javax.swing.SwingUtilities;


public abstract class SwingWorker {
  private Object _value;  


  
  private static class ThreadVar {
    private Thread _thread;
    ThreadVar(Thread t) { _thread = t; }
    synchronized Thread get() { return _thread; }
    synchronized void clear() { _thread = null; }
  }

  private ThreadVar _threadVar;

  
  protected synchronized Object getValue() { return _value; }

  
  private synchronized void setValue(Object x) { _value = x; }

  
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
          
          SwingUtilities.invokeLater(new Runnable() {
            public void run() { throw e; }
          });
          throw e;
        }
        catch (final Error e) {
          
          SwingUtilities.invokeLater(new Runnable() {
            public void run() { throw e; }
          });
          throw e;
        }
        finally { _threadVar.clear(); }

        SwingUtilities.invokeLater(doFinished);
      }
    };

    Thread t = new Thread(doConstruct);
    _threadVar = new ThreadVar(t);
  }

  
  public void start() {
    Thread t = _threadVar.get();
    if (t != null) {
      t.start();
    }
  }
}
