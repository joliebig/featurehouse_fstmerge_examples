

package edu.rice.cs.plt.recur;

import edu.rice.cs.plt.lambda.ResolvingThunk;
import edu.rice.cs.plt.lambda.Lambda;


public interface Continuation<T> extends ResolvingThunk<T> {
  
  
  public T value();
  
  
  public boolean isResolved();
  
  
  public Continuation<? extends T> step();
  
  
  public <R> Continuation<? extends R> compose(Lambda<? super T, ? extends Continuation<? extends R>> c);
}
