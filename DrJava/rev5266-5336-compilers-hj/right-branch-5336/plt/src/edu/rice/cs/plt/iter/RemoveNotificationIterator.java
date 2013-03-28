

package edu.rice.cs.plt.iter;

import java.util.Iterator;
import edu.rice.cs.plt.lambda.Runnable1;
import edu.rice.cs.plt.object.Composite;
import edu.rice.cs.plt.object.ObjectUtil;


public class RemoveNotificationIterator<T> implements Iterator<T>, Composite {
  
  private final Iterator<? extends T> _i;
  private final Runnable1<? super T> _listener;
  private T _last; 
  
  public RemoveNotificationIterator(Iterator<? extends T> i, Runnable1<? super T> listener) {
    _i = i;
    _listener = listener;
    _last = null;
  }
  
  public int compositeHeight() { return ObjectUtil.compositeHeight(_i) + 1; }
  public int compositeSize() { return ObjectUtil.compositeSize(_i) + 1; }
    
  public boolean hasNext() { return _i.hasNext(); }
  
  public T next() {
    _last = _i.next();
    return _last;
  }
  
  public void remove() {
    _i.remove(); 
    _listener.run(_last);
  }
  
  
  public static <T> RemoveNotificationIterator<T> make(Iterator<? extends T> i,
                                                       Runnable1<? super T> listener) {
    return new RemoveNotificationIterator<T>(i, listener);
  }
    
}
