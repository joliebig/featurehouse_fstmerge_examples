

package edu.rice.cs.plt.collect;

import java.io.Serializable;
import java.util.Iterator;
import java.util.NoSuchElementException;
import edu.rice.cs.plt.iter.AbstractIterable;
import edu.rice.cs.plt.iter.SizedIterable;
import edu.rice.cs.plt.iter.EmptyIterator;
import edu.rice.cs.plt.iter.ReadOnlyIterator;
import edu.rice.cs.plt.lambda.Predicate;
import edu.rice.cs.plt.lambda.Lambda;
import edu.rice.cs.plt.object.Composite;


public abstract class ConsList<T> extends AbstractIterable<T> implements SizedIterable<T>, Composite, Serializable {
  
  public abstract <Ret> Ret apply(ConsVisitor<? super T, ? extends Ret> visitor);
  
  public abstract Iterator<T> iterator();
  
  
  public abstract boolean isEmpty();
  
  
  public abstract int size();
  
  
  public abstract int size(int bound);
  
  
  public boolean isInfinite() { return false; }
  
  
  public boolean hasFixedSize() { return true; }
  
  
  public boolean isStatic() { return true; }
  
  public int compositeHeight() { return size(); }
  public int compositeSize() { return size() + 1;  }
  
  
  public static <T> Empty<T> empty() { return Empty.<T>make(); }
    
  
  public static <T> Nonempty<T> cons(T first, ConsList<? extends T> rest) {
    return new Nonempty<T>(first, rest);
  }
  
  
  public static <T> Nonempty<T> singleton(T value) {
    return new Nonempty<T>(value, Empty.<T>make());
  }
  
  
  public static <T> T first(ConsList<? extends T> list) { return list.apply(ConsVisitor.<T>first()); }
  
  
  public static <T> ConsList<? extends T> rest(ConsList<? extends T> list) { return list.apply(ConsVisitor.<T>rest()); }
  
  
  public static <T> ConsList<? extends T> reverse(ConsList<? extends T> list) {
    return list.apply(ConsVisitor.<T>reverse());
  }
  
  
  public static <T> ConsList<? extends T> append(ConsList<? extends T> l1, ConsList<? extends T> l2) {
    return l1.apply(ConsVisitor.append(l2));
  }
  
  
  public static <T> ConsList<? extends T> filter(ConsList<? extends T> list, Predicate<? super T> pred) {
    return list.apply(ConsVisitor.<T>filter(pred));
  }
  
  
  public static <S, T> ConsList<? extends T> map(ConsList<? extends S> list, 
                                                 Lambda<? super S, ? extends T> lambda) {
    return list.apply(ConsVisitor.map(lambda));
  }
  
  
  
  public static class Empty<T> extends ConsList<T> {
    
    
    private Empty() {}
    
    private static final Empty<Void> INSTANCE = new Empty<Void>();
    
    
    @SuppressWarnings("unchecked")
    public static <T> Empty<T> make() { return (Empty<T>) INSTANCE; }
    
    
    public <Ret> Ret apply(ConsVisitor<? super T, ? extends Ret> visitor) {
      return visitor.forEmpty();
    }
    
    
    public Iterator<T> iterator() { return EmptyIterator.make(); }
    
    
    public boolean isEmpty() { return true; }
    
    
    public int size() { return 0; }
    
    
    public int size(int bound) { return 0; }
    
  }
  
  
  
  public static class Nonempty<T> extends ConsList<T> {
    
    private final T _first;
    private final ConsList<? extends T> _rest;
    
    public Nonempty(T first, ConsList<? extends T> rest) {
      _first = first;
      _rest = rest;
    }
    
    public T first() { return _first; }
    
    public ConsList<? extends T> rest() { return _rest; }
    
    
    public <Ret> Ret apply(ConsVisitor<? super T, ? extends Ret> visitor) {
      return visitor.forNonempty(_first, _rest);
    }
    
    
    public Iterator<T> iterator() {
      return new ReadOnlyIterator<T>() {
        private ConsList<? extends T> _current = Nonempty.this;

        public boolean hasNext() { return !_current.isEmpty(); }

        public T next() {
          return _current.apply(new ConsVisitor<T, T>() {
            public T forEmpty() { throw new NoSuchElementException(); }
            public T forNonempty(T first, ConsList<? extends T> rest) { 
              _current = rest;
              return first;
            }
          });
        }
      };
    }
    
    
    public boolean isEmpty() { return false; }
    
    
    public int size() { return 1 + _rest.size(); }
    
    
    public int size(int bound) {
      if (bound == 0) { return 0; }
      else { return 1 + _rest.size(bound - 1); }
    }
    
  }

}
