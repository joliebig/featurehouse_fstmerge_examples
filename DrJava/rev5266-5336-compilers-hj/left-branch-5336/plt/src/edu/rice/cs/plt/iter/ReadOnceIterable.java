

package edu.rice.cs.plt.iter;

import java.util.Iterator;


public class ReadOnceIterable<T> implements Iterable<T> {
  
  private final Iterator<T> _iter;
  public ReadOnceIterable(Iterator<T> iter) { _iter = iter; }
  public Iterator<T> iterator() { return _iter; }
  
  
  public static <T> ReadOnceIterable<T> make(Iterator<T> iter) { 
    return new ReadOnceIterable<T>(iter);
  }
  
}
