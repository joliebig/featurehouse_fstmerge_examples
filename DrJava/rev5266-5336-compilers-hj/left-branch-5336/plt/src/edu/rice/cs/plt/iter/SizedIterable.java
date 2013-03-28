

package edu.rice.cs.plt.iter;


public interface SizedIterable<T> extends Iterable<T> {
  
  public boolean isEmpty();
  
  public int size();
  
  
  public int size(int bound);
  
  
  public boolean isInfinite();
  
  
  public boolean hasFixedSize();
  
  
  public boolean isStatic();
  
}
