

package edu.rice.cs.plt.recur;

import java.util.LinkedList;
import edu.rice.cs.plt.collect.Multiset;
import edu.rice.cs.plt.collect.HashMultiset;
import edu.rice.cs.plt.tuple.Wrapper;
import edu.rice.cs.plt.tuple.IdentityWrapper;
import edu.rice.cs.plt.lambda.Runnable1;
import edu.rice.cs.plt.lambda.Thunk;
import edu.rice.cs.plt.lambda.Lambda;


public class RecursionStack<T> {
  
  private final Lambda<? super T, ? extends Wrapper<T>> _wrapperFactory;
  private final Multiset<Wrapper<T>> _previous;
  private final LinkedList<Wrapper<T>> _stack;
  
  
  public RecursionStack() { this(IdentityWrapper.<T>factory()); }
  
  
  public RecursionStack(Lambda<? super T, ? extends Wrapper<T>> wrapperFactory) {
    _wrapperFactory = wrapperFactory;
    _previous = new HashMultiset<Wrapper<T>>();
    _stack = new LinkedList<Wrapper<T>>();
  }
  
  
  public boolean contains(T arg) { return _previous.contains(_wrapperFactory.value(arg)); }
  
  
  public boolean contains(T arg, int threshold) {
    return _previous.count(_wrapperFactory.value(arg)) >= threshold;
  }
  
  
  public void push(T arg) {
    Wrapper<T> wrapped = _wrapperFactory.value(arg);
    _stack.addLast(wrapped);
    _previous.add(wrapped);
  }
  
  
  public void pop(T arg) {
    Wrapper<T> wrapped = _wrapperFactory.value(arg);
    if (_stack.isEmpty() || !_stack.getLast().equals(wrapped)) {
      throw new IllegalArgumentException("arg is not on top of the stack");
    }
    _stack.removeLast();
    _previous.remove(wrapped);
  }
  
  
  public int size() { return _stack.size(); }
  
  
  public boolean isEmpty() { return _stack.isEmpty(); }
  
  
  public void run(Runnable r, T arg) {
    if (!contains(arg)) { 
      push(arg);
      try { r.run(); }
      finally { pop(arg); }
    }
  }
  
  
  public void run(Runnable r, T arg, int threshold) {
    if (!contains(arg, threshold)) { 
      push(arg);
      try { r.run(); }
      finally { pop(arg); }
    }
  }
  
  
  public void run(Runnable r, Runnable infiniteCase, T arg) {
    Runnable toRun = (contains(arg) ? infiniteCase : r);
    push(arg);
    try { toRun.run(); }
    finally { pop(arg); }
  }
  
  
  public void run(Runnable r, Runnable infiniteCase, T arg, int threshold) {
    Runnable toRun = (contains(arg, threshold) ? infiniteCase : r);
    push(arg);
    try { toRun.run(); }
    finally { pop(arg); }
  }
  
  
  public <V extends T> void run(Runnable1<? super V> r, V arg) {
    if (!contains(arg)) { 
      push(arg);
      try { r.run(arg); }
      finally { pop(arg); }
    }
  }
  
  
  public <V extends T> void run(Runnable1<? super V> r, V arg, int threshold) {
    if (!contains(arg, threshold)) { 
      push(arg);
      try { r.run(arg); }
      finally { pop(arg); }
    }
  }
  
  
  public <V extends T> void run(Runnable1<? super V> r, Runnable1<? super V> infiniteCase, V arg) {
    
    @SuppressWarnings("unchecked") Runnable1<? super V> toRun = 
      (Runnable1<? super V>) (contains(arg) ? infiniteCase : r);
    push(arg);
    try { toRun.run(arg); }
    finally { pop(arg); }
  }
  
  
  public <V extends T> void run(Runnable1<? super V> r, Runnable1<? super V> infiniteCase, V arg, 
                                int threshold) {
    
    @SuppressWarnings("unchecked") Runnable1<? super V> toRun = 
      (Runnable1<? super V>) (contains(arg, threshold) ? infiniteCase : r);
    push(arg);
    try { toRun.run(arg); }
    finally { pop(arg); }
  }
  
  
  public <R> R apply(Thunk<? extends R> thunk, R infiniteCase, T arg) {
    if (!contains(arg)) { 
      push(arg);
      try { return thunk.value(); }
      finally { pop(arg); }
    }
    else { return infiniteCase; }
  }
  
  
  public <R> R apply(Thunk<? extends R> thunk, R infiniteCase, T arg, int threshold) {
    if (!contains(arg, threshold)) { 
      push(arg);
      try { return thunk.value(); }
      finally { pop(arg); }
    }
    else { return infiniteCase; }
  }
  
  
  public <R> R apply(Thunk<? extends R> thunk, Thunk<? extends R> infiniteCase, T arg) {
    Thunk<? extends R> toApply = (contains(arg) ? infiniteCase : thunk);
    push(arg);
    try { return toApply.value(); }
    finally { pop(arg); }
  }
  
  
  public <R> R apply(Thunk<? extends R> thunk, Thunk<? extends R> infiniteCase, T arg, 
                     int threshold) {
    Thunk<? extends R> toApply = (contains(arg, threshold) ? infiniteCase : thunk);
    push(arg);
    try { return toApply.value(); }
    finally { pop(arg); }
  }
  
  
  public <V extends T, R> R apply(Lambda<? super V, ? extends R> lambda, R infiniteCase, V arg) {
    if (!contains(arg)) { 
      push(arg);
      try { return lambda.value(arg); }
      finally { pop(arg); }
    }
    else { return infiniteCase; }
  }
  
  
  public <V extends T, R> R apply(Lambda<? super V, ? extends R> lambda, R infiniteCase, V arg, 
                                  int threshold) {
    if (!contains(arg, threshold)) { 
      push(arg);
      try { return lambda.value(arg); }
      finally { pop(arg); }
    }
    else { return infiniteCase; }
  }
  
  
  public <V extends T, R> R apply(Lambda<? super V, ? extends R> lambda, 
                                  Lambda<? super V, ? extends R> infiniteCase, V arg) {
    
    @SuppressWarnings("unchecked") Lambda<? super V, ? extends R> toApply = 
      (Lambda<? super V, ? extends R>) (contains(arg) ? infiniteCase : lambda);
    push(arg);
    try { return toApply.value(arg); }
    finally { pop(arg); }
  }
  
  
  public <V extends T, R> R apply(Lambda<? super V, ? extends R> lambda, 
                                  Lambda<? super V, ? extends R> infiniteCase, V arg, 
                                  int threshold) {
    
    @SuppressWarnings("unchecked") Lambda<? super V, ? extends R> toApply = 
      (Lambda<? super V, ? extends R>) (contains(arg, threshold) ? infiniteCase : lambda);
    push(arg);
    try { return toApply.value(arg); }
    finally { pop(arg); }
  }
  
  
  public static <T> RecursionStack<T> make() { return new RecursionStack<T>(); }
  
  
  public static <T> RecursionStack<T> make(Lambda<? super T, ? extends Wrapper<T>> wrapperFactory) {
    return new RecursionStack<T>(wrapperFactory);
  }
  
}
