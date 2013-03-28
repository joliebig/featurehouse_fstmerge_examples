

package edu.rice.cs.plt.iter;

import edu.rice.cs.plt.lambda.Lambda;
import edu.rice.cs.plt.lambda.LambdaUtil;


public class FiniteSequenceIterable<T> extends TruncatedIterable<T> {
  
  
  public FiniteSequenceIterable(T initial, Lambda<? super T, ? extends T> successor, int size) {
    super(new SequenceIterable<T>(initial, successor), size);
  }
  
  public int size() { return _size; }
  public int size(int bound) { return _size <= bound ? _size : bound; }
  public boolean hasFixedSize() { return true; }
  
  public boolean isStatic() { return false; }
  
  
  public static <T> FiniteSequenceIterable<T> make(T initial, Lambda<? super T, ? extends T> successor, 
                                                   int size) { 
    return new FiniteSequenceIterable<T>(initial, successor, size);
  }
  
  
  public static <T> SnapshotIterable<T> makeSnapshot(T initial, Lambda<? super T, ? extends T> successor, 
                                                     int size) { 
    return new SnapshotIterable<T>(new FiniteSequenceIterable<T>(initial, successor, size));
  }
  
  
  public static FiniteSequenceIterable<Integer> makeIntegerSequence(int start, int end) {
    if (start <= end) {
      return new FiniteSequenceIterable<Integer>(start, LambdaUtil.INCREMENT_INT, end-start+1);
    }
    else {
      return new FiniteSequenceIterable<Integer>(start, LambdaUtil.DECREMENT_INT, start-end+1);
    }
  }
  
  
  public static <T> FiniteSequenceIterable<T> makeCopies(T value, int copies) {
    return new FiniteSequenceIterable<T>(value, LambdaUtil.<T>identity(), copies);
  }
  
}
