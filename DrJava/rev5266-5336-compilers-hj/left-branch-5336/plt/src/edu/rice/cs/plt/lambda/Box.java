

package edu.rice.cs.plt.lambda;


public interface Box<T> extends Thunk<T> {
  public void set(T val);
}
