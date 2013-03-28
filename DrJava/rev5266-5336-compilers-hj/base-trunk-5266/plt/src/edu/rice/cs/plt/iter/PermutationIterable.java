

package edu.rice.cs.plt.iter;

import java.io.Serializable;
import edu.rice.cs.plt.object.Composite;
import edu.rice.cs.plt.object.ObjectUtil;


public class PermutationIterable<T> extends AbstractIterable<Iterable<T>> 
                                    implements SizedIterable<Iterable<T>>, OptimizedLastIterable<Iterable<T>>,
                                               Composite, Serializable {
  
  private final Iterable<? extends T> _original;
  
  public PermutationIterable(Iterable<? extends T> original) { _original = original; }
  public PermutationIterator<T> iterator() { return new PermutationIterator<T>(_original); }
  
  public int compositeHeight() { return ObjectUtil.compositeHeight((Object) _original) + 1; }
  public int compositeSize() { return ObjectUtil.compositeSize((Object) _original) + 1; }
    
  public boolean isEmpty() { return false; }

  public int size() { return size(Integer.MAX_VALUE); }
  
  public int size(int bound) {
    int n = IterUtil.sizeOf(_original, bound);
    long result = 1; 
    for (int i = 2; i < n && result < bound; i++) { result *= i; }
    return result <= bound ? (int) result : bound;
  }
  
  public boolean isInfinite() { return IterUtil.isInfinite(_original); }
  
  public boolean hasFixedSize() { return IterUtil.hasFixedSize(_original); }
  
  public boolean isStatic() { return IterUtil.isStatic(_original); }
  
  public Iterable<T> last() { return IterUtil.reverse(_original); }
  
  
  public static <T> PermutationIterable<T> make(Iterable<? extends T> original) {
    return new PermutationIterable<T>(original);
  }
  
  
  public static <T> SnapshotIterable<Iterable<T>> makeSnapshot(Iterable<? extends T> original) {
    return new SnapshotIterable<Iterable<T>>(new PermutationIterable<T>(original));
  }
  
}
