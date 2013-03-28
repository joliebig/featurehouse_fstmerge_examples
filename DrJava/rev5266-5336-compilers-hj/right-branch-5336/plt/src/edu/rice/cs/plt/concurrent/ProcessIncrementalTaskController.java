

package edu.rice.cs.plt.concurrent;

import java.io.*;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Executor;

import edu.rice.cs.plt.io.IOUtil;
import edu.rice.cs.plt.iter.IterUtil;
import edu.rice.cs.plt.lambda.Runnable1;
import edu.rice.cs.plt.lambda.WrappedException;

import static edu.rice.cs.plt.debug.DebugUtil.error;


public class ProcessIncrementalTaskController<I, R> extends IncrementalTaskController<I, R> {

  
  private JVMBuilder _jvmBuilder;
  private Executor _executor;
  private IncrementalTask<? extends I, ? extends R> _task;
  
  private Runnable1<? super Process> _onExit;
  
  private volatile Thread _t;
  private volatile ObjectOutputStream _commandSink;

  
  public ProcessIncrementalTaskController(JVMBuilder jvmBuilder, Executor executor,
                                          IncrementalTask<? extends I, ? extends R> task,
                                          boolean ignoreIntermediate) {
    super(ignoreIntermediate);
    _jvmBuilder = jvmBuilder;
    _executor = executor;
    _task = task;
    _onExit = null;
    _t = null;
    _commandSink = null;
  }
  
  
  public ProcessIncrementalTaskController(JVMBuilder jvmBuilder,
                                          Executor executor,
                                          IncrementalTask<? extends I, ? extends R> task,
                                          boolean ignoreIntermediate,
                                          Runnable1<? super Process> onExit) {
    super(ignoreIntermediate);
    _jvmBuilder = jvmBuilder;
    _executor = executor;
    _task = task;
    _onExit = onExit;
    _t = null;
    _commandSink = null;
  }
  
  
  protected void doStart() {
    _executor.execute(new Runnable() {
      public void run() {
        _t = Thread.currentThread();
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
            
            ObjectInputStream objIn = new ObjectInputStream(in);
            try {
              ObjectOutputStream objOut = new ObjectOutputStream(p.getOutputStream());
              try {
                objOut.writeObject(_task);
                objOut.writeObject(Command.RUN);
                objOut.flush();
                _commandSink = objOut;
                
                Result r;
                do {
                  r = (Result) objIn.readObject();
                  r.handle(ProcessIncrementalTaskController.this);
                } while (!(r instanceof FinishResult));
                if (r instanceof CleanFinishResult) {
                  
                  Runnable1<? super Process> onExit = _onExit; 
                  if (onExit != null) { p.waitFor(); onExit.run(p); }
                }
                else { p.destroy(); }
              }
              finally { objOut.close(); }
            }
            finally { objIn.close(); }
          }
          catch (EOFException e) {
            p.destroy();
            throw new IOException("Unable to run process; class path may need to be adjusted");
          }
          
          catch (Throwable e) { p.destroy(); throw e; }
        }
        catch (InterruptedException e) {  }
        catch (InterruptedIOException e) {  }
        catch (RuntimeException e) { finishedWithImplementationException(e); }
        catch (Throwable t) { finishedWithImplementationException(new WrappedException(t)); }
      }
    });
  }
  
  protected void doStop() { writeCommand(Command.CANCEL); }
  protected void doPause() { writeCommand(Command.PAUSE); }
  protected void doResume() { writeCommand(Command.RUN); }
  
  private void writeCommand(Command c) {
    try { _commandSink.writeObject(c); _commandSink.flush(); }
    catch (IOException e) {
      finishedWithImplementationException(new WrappedException(e));
      _t.interrupt();
    }
  }
  
  protected void discard() {
    _jvmBuilder = null;
    _executor = null;
    _task = null;
    _onExit = null;
    _t = null;
    _commandSink = null;
  }
  
  
  private static enum Command { RUN, PAUSE, CANCEL; }
  
  
  private static abstract class Result implements Serializable {
    public abstract <I, R> void handle(ProcessIncrementalTaskController<I, R> c);
  }
  
  private static class StartedResult extends Result {
    public <I, R> void handle(ProcessIncrementalTaskController<I, R> c) { c.started(); }
  }
  
  private static class PausedResult extends Result {
    public <I, R> void handle(ProcessIncrementalTaskController<I, R> c) { c.paused(); }
  }

  private static class StepResult extends Result {
    private final Object _value;
    public StepResult(Object value) { _value = value; }
    
    @SuppressWarnings("unchecked") public <I, R> void handle(ProcessIncrementalTaskController<I, R> c) {
      c.stepped((I) _value);
    }
  }
  
  private static abstract class FinishResult extends Result {}
  
  private static class CleanFinishResult extends FinishResult {
    private final Object _value;
    public CleanFinishResult(Object value) { _value = value; }
    
    @SuppressWarnings("unchecked") public <I, R> void handle(ProcessIncrementalTaskController<I, R> c) {
      c.finishedCleanly((R) _value);
    }
  }

  private static class TaskExceptionResult extends FinishResult {
    private final Exception _e;
    public TaskExceptionResult(Exception e) { _e = e; }
    public <I, R> void handle(ProcessIncrementalTaskController<I, R> c) { c.finishedWithTaskException(_e); }
  }

  private static class ImplementationExceptionResult extends FinishResult {
    private final RuntimeException _e;
    public ImplementationExceptionResult(Throwable t) {
      if (t instanceof RuntimeException) { _e = (RuntimeException) t; }
      else { _e = new WrappedException(t); }
    }
    public <I, R> void handle(ProcessIncrementalTaskController<I, R> c) { c.finishedWithImplementationException(_e); }
  }
  
  private static class CanceledResult extends FinishResult {
    public <I, R> void handle(ProcessIncrementalTaskController<I, R> c) { c.stopped(); }
  }

  
  private static class Runner {
    
    public static final byte[] PREFIX = { 0x00, 0x7f, 0x03, -0x80 };
    
    private final IncrementalTask<?, ?> _task;
    private final ObjectOutputStream _objOut;
    private final ObjectInputStream _objIn;
    private final CompletionMonitor _continueMonitor;
    private final BlockingQueue<Result> _results; 
    private final Thread _taskThread; 
    private final Thread _objInReader; 
    
    public Runner(IncrementalTask<?, ?> task, ObjectOutputStream objOut, ObjectInputStream objIn) {
      _task = task;
      _objOut = objOut;
      _objIn = objIn;
      _continueMonitor = new CompletionMonitor(false);
      _results = new ArrayBlockingQueue<Result>(256);
      
      _taskThread = new Thread("task runner") {
        public void run() {
          try {
            try {
              while (!_task.isResolved()) {
                authorizeContinue();
                _results.put(new StepResult(_task.step()));
              }
              authorizeContinue();
              _results.put(new CleanFinishResult(_task.value()));
            }
            catch (InterruptedException e) { _results.put(new CanceledResult()); }
            catch (WrappedException e) {
              if (e.getCause() instanceof InterruptedException) { _results.put(new CanceledResult()); }
              else { _results.put(new TaskExceptionResult(e)); }
            }
            catch (RuntimeException e) { _results.put(new TaskExceptionResult(e)); }
            catch (Throwable t) { _results.put(new ImplementationExceptionResult(t)); }
          }
          catch (InterruptedException e) {  }
        }
      };
      
      _objInReader = new Thread("objIn reader") {
        public void run() {
          try {
            try {
              while (!Thread.interrupted()) {
                Command c = (Command) _objIn.readObject();
                switch (c) {
                  case RUN: _continueMonitor.signal(); break;
                  case PAUSE: _continueMonitor.reset(); break;
                  case CANCEL: _taskThread.interrupt(); break;
                }
              }
            }
            catch (InterruptedIOException e) {  }
            catch (Throwable t) { _results.put(new ImplementationExceptionResult(t)); }
          }
          catch (InterruptedException e) {  }
        }
      };
    }
    
    public void run() throws IOException, InterruptedException {
      
      
      _objInReader.start();
      _taskThread.start();
      try {
        Result r;
        do {
          r = _results.take();
          _objOut.writeObject(r);
          _objOut.flush();
        } while (!(r instanceof FinishResult));
      }
      finally {
        
        _objInReader.interrupt();
        _taskThread.interrupt();
      }
    }
    
    private void authorizeContinue() throws InterruptedException {
      if (Thread.interrupted()) { throw new InterruptedException(); }
      if (!_continueMonitor.isSignaled()) {
        _results.put(new PausedResult());
        _continueMonitor.ensureSignaled();
        _results.put(new StartedResult());
      }
    }
    
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
          objOut.writeObject(new StartedResult());
          objOut.flush();
          ObjectInputStream objIn = new ObjectInputStream(System.in);
          try {
            IncrementalTask<?, ?> task = (IncrementalTask<?, ?>) objIn.readObject();
            Runner runner = new Runner(task, objOut, objIn);
            runner.run();
          }
          finally { objIn.close(); }
        }
        catch (RuntimeException e) { objOut.writeObject(new ImplementationExceptionResult(e)); }
        catch (Throwable t) { objOut.writeObject(new ImplementationExceptionResult(t)); }
        finally { objOut.close(); }
      }
      catch (IOException e) { error.log("Error writing to System.out", e); }
    }

  }

}
