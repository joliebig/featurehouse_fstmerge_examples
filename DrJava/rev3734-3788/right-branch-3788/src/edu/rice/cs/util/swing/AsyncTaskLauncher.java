

package edu.rice.cs.util.swing;

import javax.swing.ProgressMonitor;


public abstract class AsyncTaskLauncher {
  
  
  protected abstract boolean shouldSetEnabled();
  
  
  protected abstract void setParentContainerEnabled(boolean enabled);
  
  
  protected abstract IAsyncProgress createProgressMonitor(String description, int min, int max);
  
  
  public <P, R> void executeTask(final AsyncTask<P, R> task, final P param, final boolean showProgress,
                                 final boolean lockUI) {
    Runnable uiInit = new Runnable() {
      public void run() {
        final boolean shouldUnlockUI = shouldSetEnabled() && lockUI;
        final IAsyncProgress monitor = createProgressMonitor(task.getDiscriptionMessage(), 
                                                             task.getMinProgress(), 
                                                             task.getMaxProgress());
        if (shouldSetEnabled() && lockUI) {
          setParentContainerEnabled(false);
        }
        
        Thread taskThread = new Thread(new Runnable() {
          public void run() {
            R result = null;
            Exception caughtException = null;
            try {
              result = task.runAsync(param, monitor);
            } catch (Exception e) {
              caughtException = e;
            }
            
            final AsyncCompletionArgs<R> args = new AsyncCompletionArgs<R>(result, caughtException, monitor
                                                                             .isCanceled());
            
            Runnable cleanup = new Runnable() {
              public void run() {
                task.complete(args);
                
                if (shouldUnlockUI) {
                  setParentContainerEnabled(true);
                }
              }
            };
            
            Utilities.invokeLater(cleanup);
          }
        }, "Task Thread - " + task.getName());
        
        taskThread.start();
      }
    };
    
    Utilities.invokeLater(uiInit);
  }
}
