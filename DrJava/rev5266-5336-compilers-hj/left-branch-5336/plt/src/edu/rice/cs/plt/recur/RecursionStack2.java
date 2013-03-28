

package edu.rice.cs.plt.recur;

import java.util.LinkedList;
import edu.rice.cs.plt.collect.Multiset;
import edu.rice.cs.plt.collect.HashMultiset;
import edu.rice.cs.plt.tuple.Pair;
import edu.rice.cs.plt.tuple.IdentityPair;
import edu.rice.cs.plt.lambda.Runnable2;
import edu.rice.cs.plt.lambda.Thunk;
import edu.rice.cs.plt.lambda.Lambda2;


public class RecursionStack2<T1, T2> {
  
  private final Lambda2<? super T1, ? super T2, ? extends Pair<T1, T2>> _pairFactory;
  private final Multiset<Pair<T1, T2>> _previous;
  private final LinkedList<Pair<T1, T2>> _stack;
  
  
  public RecursionStack2() { this(IdentityPair.<T1, T2>factory()); }
  
  
  public RecursionStack2(Lambda2<? super T1, ? super T2, ? extends Pair<T1, T2>> pairFactory) {
    _pairFactory = pairFactory;
    _previous = new HashMultiset<Pair<T1, T2>>();
    _stack = new LinkedList<Pair<T1, T2>>();
  }
  
  
  public boolean contains(T1 arg1, T2 arg2) {
    return _previous.contains(_pairFactory.value(arg1, arg2));
  }
  
  
  public boolean contains(T1 arg1, T2 arg2, int threshold) {
    return _previous.count(_pairFactory.value(arg1, arg2)) >= threshold;
  }
  
  
  public void push(T1 arg1, T2 arg2) {
    Pair<T1, T2> wrapped = _pairFactory.value(arg1, arg2);
    _stack.addLast(wrapped);
    _previous.add(wrapped);
  }
  
  
  public void pop(T1 arg1, T2 arg2) {
    Pair<T1, T2> wrapped = _pairFactory.value(arg1, arg2);
    if (_stack.isEmpty() || !_stack.getLast().equals(wrapped)) {
      throw new IllegalArgumentException("given args are not on top of the stack");
    }
    _stack.removeLast();
    _previous.remove(wrapped);
  }
  
  
  public int size() { return _stack.size(); }
  
  
  public boolean isEmpty() { return _stack.isEmpty(); }
  
  
  public void run(Runnable r, T1 arg1, T2 arg2) {
    if (!contains(arg1, arg2)) { 
      push(arg1, arg2);
      try { r.run(); }
      finally { pop(arg1, arg2); }
    }
  }
  
  
  public void run(Runnable r, T1 arg1, T2 arg2, int threshold) {
    if (!contains(arg1, arg2, threshold)) { 
      push(arg1, arg2);
      try { r.run(); }
      finally { pop(arg1, arg2); }
    }
  }
  
  
  public void run(Runnable r, Runnable infiniteCase, T1 arg1, T2 arg2) {
    Runnable toRun = (contains(arg1, arg2) ? infiniteCase : r);
    push(arg1, arg2);
    try { toRun.run(); }
    finally { pop(arg1, arg2); }
  }
  
  
  public void run(Runnable r, Runnable infiniteCase, T1 arg1, T2 arg2, int threshold) {
    Runnable toRun = (contains(arg1, arg2, threshold) ? infiniteCase : r);
    push(arg1, arg2);
    try { toRun.run(); }
    finally { pop(arg1, arg2); }
  }
  
  
  public <V1 extends T1, V2 extends T2> void run(Runnable2<? super V1, ? super V2> r, V1 arg1, 
                                                 V2 arg2) {
    if (!contains(arg1, arg2)) { 
      push(arg1, arg2);
      try { r.run(arg1, arg2); }
      finally { pop(arg1, arg2); }
    }
  }
  
  
  public <V1 extends T1, V2 extends T2> void run(Runnable2<? super V1, ? super V2> r, V1 arg1, 
                                                 V2 arg2, int threshold) {
    if (!contains(arg1, arg2, threshold)) { 
      push(arg1, arg2);
      try { r.run(arg1, arg2); }
      finally { pop(arg1, arg2); }
    }
  }
  
  
  public <V1 extends T1, V2 extends T2> void run(Runnable2<? super V1, ? super V2> r, 
                                                 Runnable2<? super V1, ? super V2> infiniteCase, 
                                                 V1 arg1, V2 arg2) {
    
    @SuppressWarnings("unchecked") Runnable2<? super V1, ? super V2> toRun = 
      (Runnable2<? super V1, ? super V2>) (contains(arg1, arg2) ? infiniteCase : r);
    push(arg1, arg2);
    try { toRun.run(arg1, arg2); }
    finally { pop(arg1, arg2); }
  }
  
  
  public <V1 extends T1, V2 extends T2> void run(Runnable2<? super V1, ? super V2> r, 
                                                 Runnable2<? super V1, ? super V2> infiniteCase, 
                                                 V1 arg1, V2 arg2, int threshold) {
    
    @SuppressWarnings("unchecked") Runnable2<? super V1, ? super V2> toRun = 
      (Runnable2<? super V1, ? super V2>) (contains(arg1, arg2, threshold) ? infiniteCase : r);
    push(arg1, arg2);
    try { toRun.run(arg1, arg2); }
    finally { pop(arg1, arg2); }
  }
  
  
  public <R> R apply(Thunk<? extends R> thunk, R infiniteCase, T1 arg1, T2 arg2) {
    if (!contains(arg1, arg2)) { 
      push(arg1, arg2);
      try { return thunk.value(); }
      finally { pop(arg1, arg2); }
    }
    else { return infiniteCase; }
  }
  
  
  public <R> R apply(Thunk<? extends R> thunk, R infiniteCase, T1 arg1, T2 arg2, 
                     int threshold) {
    if (!contains(arg1, arg2, threshold)) { 
      push(arg1, arg2);
      try { return thunk.value(); }
      finally { pop(arg1, arg2); }
    }
    else { return infiniteCase; }
  }
  
  
  public <R> R apply(Thunk<? extends R> thunk, Thunk<? extends R> infiniteCase, T1 arg1, 
                     T2 arg2) {
    Thunk<? extends R> toApply = (contains(arg1, arg2) ? infiniteCase : thunk);
    push(arg1, arg2);
    try { return toApply.value(); }
    finally { pop(arg1, arg2); }
  }
  
  
  public <R> R apply(Thunk<? extends R> thunk, Thunk<? extends R> infiniteCase, T1 arg1, 
                     T2 arg2, int threshold) {
    Thunk<? extends R> toApply = (contains(arg1, arg2, threshold) ? infiniteCase : thunk);
    push(arg1, arg2);
    try { return toApply.value(); }
    finally { pop(arg1, arg2); }
  }
  
  
  public <V1 extends T1, V2 extends T2, R> 
    R apply(Lambda2<? super V1, ? super V2, ? extends R> lambda, R infiniteCase, V1 arg1, V2 arg2) {
    if (!contains(arg1, arg2)) { 
      push(arg1, arg2);
      try { return lambda.value(arg1, arg2); }
      finally { pop(arg1, arg2); }
    }
    else { return infiniteCase; }
  }
  
  
  public <V1 extends T1, V2 extends T2, R> 
    R apply(Lambda2<? super V1, ? super V2, ? extends R> lambda, R infiniteCase, V1 arg1, 
            V2 arg2, int threshold) {
    if (!contains(arg1, arg2, threshold)) { 
      push(arg1, arg2);
      try { return lambda.value(arg1, arg2); }
      finally { pop(arg1, arg2); }
    }
    else { return infiniteCase; }
  }
  
  
  public <V1 extends T1, V2 extends T2, R> 
    R apply(Lambda2<? super V1, ? super V2, ? extends R> lambda, 
            Lambda2<? super V1, ? super V2, ? extends R> infiniteCase, V1 arg1, V2 arg2) {
    
    @SuppressWarnings("unchecked") Lambda2<? super V1, ? super V2, ? extends R> toApply = 
      (Lambda2<? super V1, ? super V2, ? extends R>) (contains(arg1, arg2) ? infiniteCase : lambda);
    push(arg1, arg2);
    try { return toApply.value(arg1, arg2); }
    finally { pop(arg1, arg2); }
  }
  
  
  public <V1 extends T1, V2 extends T2, R> 
    R apply(Lambda2<? super V1, ? super V2, ? extends R> lambda, 
            Lambda2<? super V1, ? super V2, ? extends R> infiniteCase, V1 arg1, V2 arg2, 
                     int threshold) {
    
    @SuppressWarnings("unchecked") Lambda2<? super V1, ? super V2, ? extends R> toApply = 
      (Lambda2<? super V1, ? super V2, ? extends R>) (contains(arg1, arg2, threshold) ? 
                                                        infiniteCase : lambda);
    push(arg1, arg2);
    try { return toApply.value(arg1, arg2); }
    finally { pop(arg1, arg2); }
  }
  
  
  public static <T1, T2> RecursionStack2<T1, T2> make() { return new RecursionStack2<T1, T2>(); }
  
  
  public static <T1, T2> RecursionStack2<T1, T2> make(Lambda2<? super T1, ? super T2, 
                                                              ? extends Pair<T1, T2>> pairFactory) {
    return new RecursionStack2<T1, T2>(pairFactory);
  }
  
}
