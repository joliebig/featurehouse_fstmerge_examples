

package edu.rice.cs.plt.iter;

import java.util.Iterator;


public class PermutationIterator<T> extends ReadOnlyIterator<Iterable<T>> {
  
  private final Iterable<? extends T> _original;
  private final Iterator<? extends T> _elements;
  private T _element;
  private int _elementIndex;
  private Iterator<Iterable<T>> _restPermutations;
  
  public PermutationIterator(Iterable<? extends T> original) { 
    _original = original;
    _elements = _original.iterator();
    
    _elementIndex = -1;
    
    
    if (IterUtil.isEmpty(_original)) { 
      _restPermutations = SingletonIterator.<Iterable<T>>make(EmptyIterable.<T>make());
    }
    else { _restPermutations = EmptyIterator.make(); }
  }
  
  public boolean hasNext() { return _restPermutations.hasNext() || _elements.hasNext(); }
  
  public Iterable<T> next() {
    
    if (IterUtil.isEmpty(_original)) { return _restPermutations.next(); }
    else {
      if (!_restPermutations.hasNext()) {
        _element = _elements.next(); 
        _elementIndex++;
        _restPermutations = new PermutationIterator<T>(makeRest(_elementIndex));
        
      }
      return new ComposedIterable<T>(_element, _restPermutations.next());
    }
  }
  
  private Iterable<T> makeRest(int skipIndex) {
    Iterable<T> result = EmptyIterable.make();
    int i = 0;
    for (T e : _original) {
      if (i != skipIndex) { result = new ComposedIterable<T>(result, e); }
      i++;
    }
    return result;
  }
  
  
  public static <T> PermutationIterator<T> make(Iterable<? extends T> original) {
    return new PermutationIterator<T>(original);
  }
}
