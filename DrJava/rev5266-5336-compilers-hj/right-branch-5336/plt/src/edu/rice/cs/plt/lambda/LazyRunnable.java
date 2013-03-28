

package edu.rice.cs.plt.lambda;


public class LazyRunnable implements Runnable {

  private Runnable _block;
  
  public LazyRunnable(Runnable block) { _block = block; }
  
  public void run() {
    
    
    
    if (_block != null) { resolve(); }
  }
  
  private synchronized void resolve() {
    if (_block != null) { 
      _block.run();
      _block = null;
    }
  }
  
}
