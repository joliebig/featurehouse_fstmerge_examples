

package edu.rice.cs.plt.iter;


public abstract class AbstractIterable<T> implements Iterable<T> {
  
  
  public String toString() { return IterUtil.toString(this); }
  
  
  public final boolean equals(Object obj) {
    if (obj instanceof AbstractIterable<?>) { return IterUtil.isEqual(this, (AbstractIterable<?>) obj); }
    else { return false; }
  }
  
  
  public final int hashCode() { return IterUtil.hashCode(this); }
}
