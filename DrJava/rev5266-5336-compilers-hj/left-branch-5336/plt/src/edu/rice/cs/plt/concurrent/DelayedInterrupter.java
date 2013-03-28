

package edu.rice.cs.plt.concurrent;

import java.util.Timer;
import java.util.TimerTask;

import edu.rice.cs.plt.lambda.LazyThunk;
import edu.rice.cs.plt.lambda.Thunk;


public class DelayedInterrupter {
  
  
  private static final LazyThunk<Timer> TIMER = new LazyThunk<Timer>(new Thunk<Timer>() {
    public Timer value() { return new Timer("DelayedInterrupter Timer", true); }
  });

  private final Thread _worker;
  private final TimerTask _task;
  
  
  public DelayedInterrupter(long timeToInterrupt) { this(Thread.currentThread(), timeToInterrupt); }
  
  
  public DelayedInterrupter(Thread worker, final long timeToInterrupt) {
    _worker = worker;
    _task = new TimerTask() {
      public void run() { _worker.interrupt(); }
    };
    TIMER.value().schedule(_task, timeToInterrupt);
  }
    
  
  public void abort() {
    _task.cancel();
    TIMER.value().purge();
    if (Thread.currentThread() == _worker) {
      Thread.interrupted(); 
    }
  }
  
}
