

package edu.rice.cs.plt.iter;


public abstract class IndexedIterator<T> extends ReadOnlyIterator<T> {
  
  private int _i;
  
  protected IndexedIterator() { _i = 0; }
  
  protected abstract int size();
  protected abstract T get(int index);
  
  public boolean hasNext() { return _i < size(); }
  
  public T next() { 
    T result = get(_i);
    _i++;
    return result;
  }
  
}
