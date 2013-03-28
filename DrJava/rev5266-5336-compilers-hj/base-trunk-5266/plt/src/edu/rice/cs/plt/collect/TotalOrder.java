

package edu.rice.cs.plt.collect;

import java.util.Comparator;


public abstract class TotalOrder<T> implements Comparator<T>, Order<T> {
  public abstract int compare(T arg1, T arg2);
  public final boolean contains(T arg1, T arg2) { return compare(arg1, arg2) >= 0; }
}
