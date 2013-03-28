

package edu.rice.cs.plt.iter;

import java.io.Serializable;
import edu.rice.cs.plt.object.Composite;
import edu.rice.cs.plt.object.ObjectUtil;


public class ComposedIterable<T> extends AbstractIterable<T> 
  implements SizedIterable<T>, OptimizedLastIterable<T>, Composite, Serializable {
  
  private final Iterable<? extends T> _i1;
  private final int _i1Size; 
  private final Iterable<? extends T> _i2;
  private final int _i2Size; 
  private final boolean _isStatic;
  
  
  public ComposedIterable(Iterable<? extends T> i1, Iterable<? extends T> i2) {
    _i1 = i1;
    _i2 = i2;
    if (IterUtil.hasFixedSize(_i1)) { _i1Size = IterUtil.sizeOf(_i1); }
    else { _i1Size = -1; }
    if (IterUtil.hasFixedSize(_i2)) { _i2Size = IterUtil.sizeOf(_i2); }
    else { _i2Size = -1; }
    _isStatic = IterUtil.isStatic(_i1) && IterUtil.isStatic(_i2);
  }
    
  
  public ComposedIterable(T v1, Iterable<? extends T> i2) {
    this(new SingletonIterable<T>(v1), i2);
  }
  
  
  public ComposedIterable(Iterable<? extends T> i1, T v2) {
    this(i1, new SingletonIterable<T>(v2));
  }
  
  public ComposedIterator<T> iterator() { 
    return new ComposedIterator<T>(_i1.iterator(), _i2.iterator());
  }
  
  public int compositeHeight() { return ObjectUtil.compositeHeight(_i1, _i2) + 1; }
  public int compositeSize() { return ObjectUtil.compositeSize(_i1, _i2) + 1; }
  
  public boolean isEmpty() {
    return (_i1Size < 0 ? IterUtil.isEmpty(_i1) : _i1Size == 0) &&
           (_i2Size < 0 ? IterUtil.isEmpty(_i2) : _i2Size == 0);
  }
  
  public int size() {
    int result = (_i1Size < 0 ? IterUtil.sizeOf(_i1) : _i1Size) +
                 (_i2Size < 0 ? IterUtil.sizeOf(_i2) : _i2Size);
    if (result < 0) { result = Integer.MAX_VALUE; } 
    return result;
  }
  
  public int size(int bound) {
    int size1 = (_i1Size < 0) ? IterUtil.sizeOf(_i1, bound) :
                                (bound < _i1Size) ? bound : _i1Size;
    int bound2 = bound-size1;
    int size2 = (_i2Size < 0) ? IterUtil.sizeOf(_i2, bound2) :
                                (bound2 < _i2Size) ? bound2 : _i2Size;
    return size1+size2;
  }
  
  public boolean isInfinite() { return IterUtil.isInfinite(_i1) || IterUtil.isInfinite(_i2); }
  
  public boolean hasFixedSize() { return _i1Size >= 0 && _i2Size >= 0; }
  
  public boolean isStatic() { return _isStatic; }
  
  
  public T last() {
    Iterable<? extends T> lastIterable;
    if (IterUtil.isEmpty(_i2)) { lastIterable = _i1; }
    else { lastIterable = _i2; }
    
    while (lastIterable instanceof ComposedIterable<?>) {
      
      @SuppressWarnings("unchecked")
      ComposedIterable<? extends T> cast = (ComposedIterable<? extends T>) lastIterable;
      if (IterUtil.isEmpty(cast._i2)) { lastIterable = cast._i1; }
      else { lastIterable = cast._i2; }
    }
    
    return IterUtil.last(lastIterable);
  }
  
  
  public static <T> ComposedIterable<T> make(Iterable<? extends T> i1, Iterable<? extends T> i2) {
    return new ComposedIterable<T>(i1, i2);
  }
  
  
  public static <T> ComposedIterable<T> make(T v1, Iterable<? extends T> i2) {
    return new ComposedIterable<T>(v1, i2);
  }
  
  
  public static <T> ComposedIterable<T> make(Iterable<? extends T> i1, T v2) {
    return new ComposedIterable<T>(i1, v2);
  }
  
}
