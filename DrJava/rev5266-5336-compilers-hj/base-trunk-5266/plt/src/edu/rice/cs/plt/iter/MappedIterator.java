

package edu.rice.cs.plt.iter;

import java.util.Iterator;
import edu.rice.cs.plt.lambda.Lambda;
import edu.rice.cs.plt.object.Composite;
import edu.rice.cs.plt.object.ObjectUtil;


public class MappedIterator<S, T> implements Iterator<T>, Composite {

  private final Iterator<? extends S> _source;
  private final Lambda<? super S, ? extends T> _map;
  
  public MappedIterator(Iterator<? extends S> source, Lambda<? super S, ? extends T> map) {
    _source = source;
    _map = map;
  }
  
  public int compositeHeight() { return ObjectUtil.compositeHeight(_source) + 1; }
  public int compositeSize() { return ObjectUtil.compositeSize(_source) + 1; }
    
  public boolean hasNext() { return _source.hasNext(); }
  public T next() { return _map.value(_source.next()); }
  public void remove() { _source.remove(); }
  
  
  public static <S, T> MappedIterator<S, T> make(Iterator<? extends S> source, 
                                                 Lambda<? super S, ? extends T> map) {
    return new MappedIterator<S, T>(source, map);
  }
  
}
