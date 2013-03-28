
package gj.shell.swing;

import java.awt.event.ActionEvent;
import java.io.CharArrayWriter;
import java.io.PrintWriter;

import javax.swing.AbstractAction;
import javax.swing.Icon;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;


public abstract class Action2 extends AbstractAction implements Runnable {

  
  public static final int 
    ASYNC_NOT_APPLICABLE = 0,
    ASYNC_SAME_INSTANCE  = 1,
    ASYNC_NEW_INSTANCE   = 2;

  
  private int asynchronous = ASYNC_NOT_APPLICABLE;
  
  
  private Thread thread;
  private Object threadLock = new Object();

  
  protected Action2() {
  }
  
  
  protected Action2(String name) {
    super(name);
  }
  
  
  public final void actionPerformed(ActionEvent e) {
    trigger();
  }
  
  
  public void run() {
    trigger();
  }
  
  
  protected void setAsync(int set) {
    asynchronous=set;
  }
  
  
  protected int getAsync() {
    return asynchronous;
  }
  
  
  public void cancel(boolean wait) {
    Thread t = getThread();
    if (t!=null&&t.isAlive()) {
      t.interrupt();
      if (wait) try {
        t.join();
      } catch (InterruptedException e) {
      }
    }
  }
  
  
  public final Action2 trigger() {
    
    int tasync = getAsync();
    
    
    if (tasync==ASYNC_NEW_INSTANCE) {
      try {
        Action2 action = (Action2)clone();
        action.setAsync(ASYNC_SAME_INSTANCE);
        return action.trigger();
      } catch (Throwable t) {
        t.printStackTrace();
        handleThrowable("trigger", new RuntimeException("Couldn't clone instance of "+getClass().getName()+" for ASYNC_NEW_INSTANCE"));
      }
      return this;
    }
    
    
    boolean preExecuteOk;
    try {
      preExecuteOk = preExecute();
    } catch (Throwable t) {
      handleThrowable("preExecute",t);
      preExecuteOk = false;
    }
    
    
    if (preExecuteOk) try {
      if (tasync!=ASYNC_NOT_APPLICABLE) {
        synchronized (threadLock) {
          getThread().start();
        }
      }
      else execute();
    } catch (Throwable t) {
      handleThrowable("execute(sync)", t);
    }
    
    
    if ((tasync==ASYNC_NOT_APPLICABLE)||(!preExecuteOk)) try {
      postExecute();
    } catch (Throwable t) {
      handleThrowable("postExecute", t);
    }
    
    
    return this;
    
  }
  
  
  public Thread getThread() {
    if (getAsync()!=ASYNC_SAME_INSTANCE) return null;
    synchronized (threadLock) {
      if (thread==null) thread=new Thread(new CallAsyncExecute());
      return thread;
    }
  }
  
  
  public void setName(String name) {
    super.putValue(NAME, name);
  }
  
  public String getName() {
    return (String)super.getValue(NAME);
  }
  
  
  public void setIcon(Icon icon) {
    super.putValue(SMALL_ICON, icon);
  }
  
  
  public boolean isSelected() {
    return false;    
  }
  
  
  protected boolean preExecute() throws Exception {
    
    return true;
  }
  
  
  protected abstract void execute() throws Exception;

  
  protected void postExecute() throws Exception {
    
  }
  
  
  protected void handleThrowable(String phase, Throwable t) {
    
    
    CharArrayWriter ca = new CharArrayWriter(256);
    t.printStackTrace(new PrintWriter(ca));
    
    SwingHelper.showDialog(
      null, 
      "Sorry", 
      new JScrollPane(new JTextArea(ca.toString(),8,32)), 
      SwingHelper.DLG_OK);
  }

  
  private class CallAsyncExecute implements Runnable {
    public void run() {
      try {
        execute();
      } catch (Throwable t) {
        SwingUtilities.invokeLater(new CallSyncHandleThrowable(t));
      }
      synchronized (threadLock) {
        thread=null;
      }
      SwingUtilities.invokeLater(new CallSyncPostExecute());
    }
  } 
  
  
  private class CallSyncHandleThrowable implements Runnable {
    private Throwable t;
    protected CallSyncHandleThrowable(Throwable set) {
      t=set;
    }
    public void run() {
      
      try {
        handleThrowable("execute(async)",t);
      } catch (Throwable t) {
      }
    }
  }

  
  private class CallSyncPostExecute implements Runnable {
    public void run() {
      
      try {
        postExecute();
      } catch (Throwable t) {
        handleThrowable("postExecute", t);
      }
    }
  } 


}
