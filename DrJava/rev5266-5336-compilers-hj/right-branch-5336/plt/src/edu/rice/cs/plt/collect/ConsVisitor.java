

package edu.rice.cs.plt.collect;

import java.io.Serializable;
import edu.rice.cs.plt.lambda.Lambda;
import edu.rice.cs.plt.lambda.Predicate;


public abstract class ConsVisitor<T, Ret> implements Lambda<ConsList<? extends T>, Ret> {
  
  
  public abstract Ret forEmpty();
  
  
  public abstract Ret forNonempty(T first, ConsList<? extends T> rest);
  
  
  public Ret value(ConsList<? extends T> list) { return list.apply(this); }
  
  
  
  @SuppressWarnings("unchecked")
  public static final <T> ConsVisitor<T, T> first() { return (First<T>) First.INSTANCE; }
  
  private static class First<T> extends ConsVisitor<T, T> implements Serializable {
    private static final First<Object> INSTANCE = new First<Object>();
    private First() {}
    public T forEmpty() {
      throw new IllegalArgumentException("Empty ConsList has no first");
    }
    public T forNonempty(T first, ConsList<? extends T> rest) {
      return first;
    }
  }
  
  
  
  @SuppressWarnings("unchecked")
    public static final <T> ConsVisitor<T, ConsList<? extends T>> rest() {
      return (Rest<T>) Rest.INSTANCE;
    }
  
  private static class Rest<T> extends ConsVisitor<T, ConsList<? extends T>> implements Serializable {
    private static final Rest<Object> INSTANCE = new Rest<Object>();
    private Rest() {}
    public ConsList<? extends T> forEmpty() {
      throw new IllegalArgumentException("Empty ConsList has no rest");
    }
    public ConsList<? extends T> forNonempty(T first, ConsList<? extends T> rest) {
      return rest;
    }
  }
  
  
  
  public static <T> ConsVisitor<T, ConsList<? extends T>> reverse() {
    return new ReverseHelper<T>(ConsList.<T>empty());
  }
  
  
  private static class ReverseHelper<T> extends ConsVisitor<T, ConsList<? extends T>> implements Serializable {
    private ConsList<? extends T> _toAppend;
    
    public ReverseHelper(ConsList<? extends T> toAppend) { _toAppend = toAppend; }
    
    public ConsList<? extends T> forEmpty() { return _toAppend; }
    
    public ConsList<? extends T> forNonempty(T first, ConsList<? extends T> rest) {
      return rest.apply(new ReverseHelper<T>(ConsList.cons(first, _toAppend)));
    }
  }
  
  
  
  public static <T> ConsVisitor<T, ConsList<? extends T>> append(final ConsList<? extends T> rest) {
    return new Append<T>(rest);
  }
  
  private static class Append<T> extends ConsVisitor<T, ConsList<? extends T>> implements Serializable {
    private final ConsList<? extends T> _toAppend;
    public Append(ConsList<? extends T> toAppend) { _toAppend = toAppend; }
    public ConsList<? extends T> forEmpty() { return _toAppend; }
    public ConsList<? extends T> forNonempty(T first, ConsList<? extends T> rest) {
      return ConsList.cons(first, rest.apply(this));
    }
  }

  
  
  public static <T> ConsVisitor<T, ConsList<T>> filter(Predicate<? super T> pred) {
    return new Filter<T>(pred);
  }
  
  private static class Filter<T> extends ConsVisitor<T, ConsList<T>> implements Serializable {
    private final Predicate<? super T> _pred;
    public Filter(Predicate<? super T> pred) { _pred = pred; }
    public ConsList<T> forEmpty() { return ConsList.empty(); }
    public ConsList<T> forNonempty(T first, ConsList<? extends T> rest) {
      if (_pred.contains(first)) { return ConsList.cons(first, rest.apply(this)); }
      else { return rest.apply(this); }
    }
  }
  
  
  
  public static <S, T> ConsVisitor<S, ConsList<T>> map(Lambda<? super S, ? extends T> lambda) {
    return new Map<S, T>(lambda);
  }

  private static class Map<S, T> extends ConsVisitor<S, ConsList<T>> implements Serializable {
    private final Lambda<? super S, ? extends T> _lambda;
    public Map(Lambda<? super S, ? extends T> lambda) { _lambda = lambda; }
    public ConsList<T> forEmpty() { return ConsList.empty(); }
    public ConsList<T> forNonempty(S first, ConsList<? extends S> rest) {
      return ConsList.cons(_lambda.value(first), rest.apply(this));
    }
  }
  
}
