

package edu.rice.cs.plt.lambda;

import java.io.Serializable;


public class SimpleBox<T> implements Box<T>, Serializable {
  
  private T _val;
  
  
  public SimpleBox(T val) { _val = val; }
  
  
  public SimpleBox() { _val = null; }
  
  public void set(T val) { _val = val; }
  public T value() { return _val; }
  
  
  public static <T> Box<T> make(T val) { return new SimpleBox<T>(val); }

  
  public static <T> Box<T> make() { return new SimpleBox<T>(); }
  
}
