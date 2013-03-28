

package edu.rice.cs.plt.swing;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.BlockingQueue;

import javax.swing.SwingUtilities;

import edu.rice.cs.plt.lambda.Runnable1;
import edu.rice.cs.plt.lambda.WrappedException;
import edu.rice.cs.plt.concurrent.CompletionMonitor;
import edu.rice.cs.plt.concurrent.IncrementalTaskController;


public abstract class SwingWorker<R, I> extends IncrementalTaskController<I, R> {
  
  private CompletionMonitor _continueMonitor;
  private Thread _workerThread;
  
  public SwingWorker() {
    super();
    _continueMonitor = new CompletionMonitor(false);
    _workerThread = new Thread("SwingWorker") {
      public void run() {
        started();
        try { finishedCleanly(doInBackground()); }
        catch (InterruptedException e) { stopped(); }
        catch (Exception e) { finishedWithTaskException(e); }
        catch (Throwable t) { finishedWithImplementationException(new WrappedException(t)); }
      }
    };
    finishListeners().add(new Runnable() {
      public void run() {
        SwingUtilities.invokeLater(new Runnable() {
          public void run() { done(); }
        });
      }
    });
    
    class IntermediateListener implements Runnable1<I> {
      public void run(I val) {
        intermediateListeners().remove(this); 
        SwingUtilities.invokeLater(new Runnable() {
          public void run() {
            List<I> vals = new LinkedList<I>();
            
            intermediateListeners().add(IntermediateListener.this);
            intermediateQueue().drainTo(vals);
            process(vals);
          }
        });
      }
    }
    @SuppressWarnings("unchecked") 
    Runnable1<I> listener = new IntermediateListener();
    intermediateListeners().add(listener);
  }
  
  
  
  
  public final void execute() { start(); }
  
  
   
    
  
  protected abstract R doInBackground() throws Exception;
  
  
  protected void process(List<I> chunks) {}
  
  
  protected void done() {}
  
  
  
  
  
  protected final void publish(I... chunks) {
    BlockingQueue<I> queue = intermediateQueue();
    try {
      for (I val : chunks) { queue.put(val); }
    }
    catch (InterruptedException e) { throw new WrappedException(e); }
  }
  
  
  protected void authorizeContinue() throws InterruptedException {
    if (Thread.interrupted()) { throw new InterruptedException(); }
    if (!_continueMonitor.isSignaled()) {
      paused();
      _continueMonitor.ensureSignaled();
      started();
    }
  }
  
  
  
  
  
  protected final void doStart() {
    _continueMonitor.signal();
    _workerThread.start();
  }
  protected final void doPause() { _continueMonitor.reset(); }
  protected final void doResume() { _continueMonitor.signal(); }
  protected final void doStop() { _workerThread.interrupt(); }
  
}
