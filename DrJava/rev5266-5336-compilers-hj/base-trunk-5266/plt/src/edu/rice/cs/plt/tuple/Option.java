

package edu.rice.cs.plt.tuple;

import java.util.Comparator;

import edu.rice.cs.plt.collect.CollectUtil;
import edu.rice.cs.plt.collect.TotalOrder;
import edu.rice.cs.plt.object.ObjectUtil;


public abstract class Option<T> extends Tuple {
  
  protected Option() {}
  
  
  public abstract <Ret> Ret apply(OptionVisitor<? super T, ? extends Ret> visitor);
  
  
  public abstract boolean isSome();
  
  
  public final boolean isNone() { return !isSome(); }
  
  
  public abstract T unwrap() throws OptionUnwrapException;
  
  
  public abstract T unwrap(T forNone);
  

  
  public static <T> Option<T> some(T val) { return new Wrapper<T>(val); }
  
  
  @SuppressWarnings("unchecked") public static <T> Option<T> none() {
    return (Option<T>) Null.INSTANCE;
  }
  
  
  @SuppressWarnings("unchecked") public static <T> Option<T> wrap(T val) {
    if (val == null) { return (Option<T>) Null.INSTANCE; }
    else { return new Wrapper<T>(val); }
  }
  
  
  public static <T> T unwrap(Option<? extends T> opt, T forNone) {
    if (opt.isSome()) { return opt.unwrap(); }
    else { return forNone; }
  }
    
  
  public static <T extends Comparable<? super T>> TotalOrder<Option<? extends T>> comparator() {
    return new OptionComparator<T>(CollectUtil.<T>naturalOrder());
  }
  
  
  public static <T> TotalOrder<Option<? extends T>> comparator(Comparator<? super T> comp) {
    return new OptionComparator<T>(comp);
  }
  
  private static final class OptionComparator<T> extends TotalOrder<Option<? extends T>> {
    private final Comparator<? super T> _comp;
    public OptionComparator(Comparator<? super T> comp) { _comp = comp; }
    public int compare(Option<? extends T> o1, Option<? extends T> o2) {
      if (o1.isSome()) { return o2.isSome() ? _comp.compare(o1.unwrap(), o2.unwrap()) : 1; }
      else { return o2.isSome() ? -1 : 0; }
    }
    public boolean equals(Object o) {
      if (this == o) { return true; }
      else if (!(o instanceof OptionComparator<?>)) { return false; }
      else {
        OptionComparator<?> cast = (OptionComparator<?>) o;
        return _comp.equals(cast._comp);
      }
    }
    public int hashCode() { return ObjectUtil.hash(OptionComparator.class, _comp); }
  }

}
