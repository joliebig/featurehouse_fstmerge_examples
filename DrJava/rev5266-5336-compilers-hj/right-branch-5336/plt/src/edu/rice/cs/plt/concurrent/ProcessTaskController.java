

package edu.rice.cs.plt.concurrent;

import static edu.rice.cs.plt.debug.DebugUtil.error;

import java.io.*;
import java.util.concurrent.Executor;

import edu.rice.cs.plt.io.IOUtil;
import edu.rice.cs.plt.iter.IterUtil;
import edu.rice.cs.plt.lambda.Runnable1;
import edu.rice.cs.plt.lambda.Thunk;
import edu.rice.cs.plt.lambda.WrappedException;


public class ProcessTaskController<R> extends TaskController<R> {
  
  
  

  
  private JVMBuilder _jvmBuilder;
  private Executor _executor;
  private Thunk<? extends R> _task;
  
  private Runnable1<? super Process> _onExit;
  
  private volatile Thread _t;

  
  public ProcessTaskController(JVMBuilder jvmBuilder, Executor executor, Thunk<? extends R> task) {
    _jvmBuilder = jvmBuilder;
    _executor = executor;
    _task = task;
    _onExit = null;
    _t = null;
  }
  
  
  public ProcessTaskController(JVMBuilder jvmBuilder, Executor executor, Thunk<? extends R> task,
                               Runnable1<? super Process> onExit) {
    _jvmBuilder = jvmBuilder;
    _executor = executor;
    _task = task;
    _onExit = onExit;
    _t = null;
  }
  
  protected void doStart() {
    _executor.execute(new Runnable() {
      public void run() {
        _t = Thread.currentThread();
        started();
        try {
          
          if (Thread.interrupted()) { throw new InterruptedException(); }
          Process p = _jvmBuilder.start(Runner.class.getName(), IterUtil.<String>empty());
          try {
            InputStream in = p.getInputStream();
            
            int matching = 0;
            while (matching < Runner.PREFIX.length) {
              int read = in.read();
              if (read == -1) { throw new EOFException("Data prefix not found"); }
              else if ((byte) read == Runner.PREFIX[matching]) { matching++; } 
              else if ((byte) read == Runner.PREFIX[0]) { matching = 1; } 
              else { matching = 0; }
            }
            
            ObjectOutputStream objOut = new ObjectOutputStream(p.getOutputStream());
            try { objOut.writeObject(_task); }
            finally { objOut.close(); }
            ObjectInputStream objIn = new ObjectInputStream(in);
            try {
              @SuppressWarnings("unchecked") R result = (R) objIn.readObject();
              Exception taskE = (Exception) objIn.readObject();
              RuntimeException implementationE = (RuntimeException) objIn.readObject();
              if (implementationE != null) { p.destroy(); finishedWithImplementationException(implementationE); }
              else if (taskE != null) { p.destroy(); finishedWithTaskException(taskE); }
              else {
                Runnable1<? super Process> onExit = _onExit; 
                finishedCleanly(result);
                if (onExit != null) {
                  p.waitFor();
                  onExit.run(p);
                }
              }
            }
            finally { objIn.close(); }
          }
          catch (EOFException e) {
            p.destroy();
            throw new IOException("Unable to run process; class path may need to be adjusted");
          }
          
          catch (Throwable e) { p.destroy(); throw e; }
        }
        catch (InterruptedException e) { stopped(); }
        catch (InterruptedIOException e) { stopped(); }
        catch (RuntimeException e) { finishedWithImplementationException(e); }
        catch (Throwable t) { finishedWithImplementationException(new WrappedException(t)); }
      }
    });
  }
  
  protected void doStop() { _t.interrupt(); }
  
  protected void discard() {
    _jvmBuilder = null;
    _executor = null;
    _task = null;
    _onExit = null;
    _t = null;
  }

  
  private static class Runner {
    
    public static final byte[] PREFIX = { 0x00, 0x03, 0x7f, -0x80 };
    
    public static void main(String... args) {
      OutputStream out = System.out;
      IOUtil.attemptClose(System.err); 
      IOUtil.ignoreSystemOut();
      IOUtil.ignoreSystemErr();
      try {
        out.write(PREFIX);
        out.flush();
        ObjectOutputStream objOut = new ObjectOutputStream(out);
        try {
          Object result = null;
          Exception taskException = null;
          RuntimeException internalException = null;
          try {
            ObjectInputStream objIn = new ObjectInputStream(System.in);
            try {
              Thunk<?> task = (Thunk<?>) objIn.readObject();
              try { result = task.value(); }
              catch (Exception e) { taskException = e; }
            }
            finally { objIn.close(); }
          }
          catch (RuntimeException e) { internalException = e; }
          catch (Throwable t) { internalException = new WrappedException(t); }
          
          objOut.writeObject(result);
          objOut.writeObject(taskException);
          objOut.writeObject(internalException);
        }
        finally { objOut.close(); }
      }
      catch (IOException e) { error.log("Error writing to System.out", e); }
    }
  }

}
