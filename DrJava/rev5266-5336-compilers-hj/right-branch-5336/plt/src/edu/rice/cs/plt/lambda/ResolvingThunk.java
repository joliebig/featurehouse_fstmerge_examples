

package edu.rice.cs.plt.lambda;


public interface ResolvingThunk<R> extends Thunk<R> {
  
  public boolean isResolved();
  
}
