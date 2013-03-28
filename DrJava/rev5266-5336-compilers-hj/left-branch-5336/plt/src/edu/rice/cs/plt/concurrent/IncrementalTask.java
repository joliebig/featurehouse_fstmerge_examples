

package edu.rice.cs.plt.concurrent;

import edu.rice.cs.plt.lambda.ResolvingThunk;


public interface IncrementalTask<I, R> extends ResolvingThunk<R> {
  
  public boolean isResolved();
  
  
  public I step();
  
  
  public R value();
}
