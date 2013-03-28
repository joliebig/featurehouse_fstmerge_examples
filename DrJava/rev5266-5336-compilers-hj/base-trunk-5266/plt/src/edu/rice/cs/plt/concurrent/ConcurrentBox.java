

package edu.rice.cs.plt.concurrent;

import java.util.concurrent.atomic.AtomicReference;
import edu.rice.cs.plt.lambda.Box;


public class ConcurrentBox<T> extends AtomicReference<T> implements Box<T> {
  
  
  public ConcurrentBox(T val) { super(val); }
  
  
  public ConcurrentBox() { super(null); }
  
  public T value() { return get(); }
  
  
  public static <T> ConcurrentBox<T> make(T val) { return new ConcurrentBox<T>(val); }

  
  public static <T> ConcurrentBox<T> make() { return new ConcurrentBox<T>(); }
  
}
