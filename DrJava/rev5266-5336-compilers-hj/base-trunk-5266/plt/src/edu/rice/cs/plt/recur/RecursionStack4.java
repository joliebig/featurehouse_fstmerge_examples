

package edu.rice.cs.plt.recur;

import java.util.LinkedList;
import edu.rice.cs.plt.collect.Multiset;
import edu.rice.cs.plt.collect.HashMultiset;
import edu.rice.cs.plt.tuple.Quad;
import edu.rice.cs.plt.tuple.IdentityQuad;
import edu.rice.cs.plt.lambda.Runnable4;
import edu.rice.cs.plt.lambda.Thunk;
import edu.rice.cs.plt.lambda.Lambda4;


public class RecursionStack4<T1, T2, T3, T4> {
  
  private final Lambda4<? super T1, ? super T2, ? super T3, ? super T4, 
                        ? extends Quad<T1, T2, T3, T4>> _quadFactory;
  private final Multiset<Quad<T1, T2, T3, T4>> _previous;
  private final LinkedList<Quad<T1, T2, T3, T4>> _stack;
  
  
  public RecursionStack4() { this(IdentityQuad.<T1, T2, T3, T4>factory()); }
  
  
  public RecursionStack4(Lambda4<? super T1, ? super T2, ? super T3, ? super T4,
                                 ? extends Quad<T1, T2, T3, T4>> quadFactory) {
    _quadFactory = quadFactory;
    _previous = new HashMultiset<Quad<T1, T2, T3, T4>>();
    _stack = new LinkedList<Quad<T1, T2, T3, T4>>();
  }
  
  
  public boolean contains(T1 arg1, T2 arg2, T3 arg3, T4 arg4) {
    return _previous.contains(_quadFactory.value(arg1, arg2, arg3, arg4));
  }
  
  
  public boolean contains(T1 arg1, T2 arg2, T3 arg3, T4 arg4, int threshold) {
    return _previous.count(_quadFactory.value(arg1, arg2, arg3, arg4)) >= threshold;
  }
  
  
  public void push(T1 arg1, T2 arg2, T3 arg3, T4 arg4) {
    Quad<T1, T2, T3, T4> wrapped = _quadFactory.value(arg1, arg2, arg3, arg4);
    _stack.addLast(wrapped);
    _previous.add(wrapped);
  }
  
  
  public void pop(T1 arg1, T2 arg2, T3 arg3, T4 arg4) {
    Quad<T1, T2, T3, T4> wrapped = _quadFactory.value(arg1, arg2, arg3, arg4);
    if (_stack.isEmpty() || !_stack.getLast().equals(wrapped)) {
      throw new IllegalArgumentException("given args are not on top of the stack");
    }
    _stack.removeLast();
    _previous.remove(wrapped);
  }
  
  
  public int size() { return _stack.size(); }
  
  
  public boolean isEmpty() { return _stack.isEmpty(); }
  
  
  public void run(Runnable r, T1 arg1, T2 arg2, T3 arg3, T4 arg4) {
    if (!contains(arg1, arg2, arg3, arg4)) { 
      push(arg1, arg2, arg3, arg4);
      try { r.run(); }
      finally { pop(arg1, arg2, arg3, arg4); }
    }
  }
  
  
  public void run(Runnable r, T1 arg1, T2 arg2, T3 arg3, T4 arg4, int threshold) {
    if (!contains(arg1, arg2, arg3, arg4, threshold)) { 
      push(arg1, arg2, arg3, arg4);
      try { r.run(); }
      finally { pop(arg1, arg2, arg3, arg4); }
    }
  }
  
  
  public void run(Runnable r, Runnable infiniteCase, T1 arg1, T2 arg2, T3 arg3, T4 arg4) {
    Runnable toRun = (contains(arg1, arg2, arg3, arg4) ? infiniteCase : r);
    push(arg1, arg2, arg3, arg4);
    try { toRun.run(); }
    finally { pop(arg1, arg2, arg3, arg4); }
  }
  
  
  public void run(Runnable r, Runnable infiniteCase, T1 arg1, T2 arg2, T3 arg3, T4 arg4, int threshold) {
    Runnable toRun = (contains(arg1, arg2, arg3, arg4, threshold) ? infiniteCase : r);
    push(arg1, arg2, arg3, arg4);
    try { toRun.run(); }
    finally { pop(arg1, arg2, arg3, arg4); }
  }
  
  
  public <V1 extends T1, V2 extends T2, V3 extends T3, V4 extends T4>
    void run(Runnable4<? super V1, ? super V2, ? super V3, ? super V4> r, V1 arg1, V2 arg2, 
             V3 arg3, V4 arg4) {
    if (!contains(arg1, arg2, arg3, arg4)) { 
      push(arg1, arg2, arg3, arg4);
      try { r.run(arg1, arg2, arg3, arg4); }
      finally { pop(arg1, arg2, arg3, arg4); }
    }
  }
  
  
  public <V1 extends T1, V2 extends T2, V3 extends T3, V4 extends T4>
    void run(Runnable4<? super V1, ? super V2, ? super V3, ? super V4> r, V1 arg1, V2 arg2, 
             V3 arg3, V4 arg4, int threshold) {
    if (!contains(arg1, arg2, arg3, arg4, threshold)) { 
      push(arg1, arg2, arg3, arg4);
      try { r.run(arg1, arg2, arg3, arg4); }
      finally { pop(arg1, arg2, arg3, arg4); }
    }
  }
  
  
  public <V1 extends T1, V2 extends T2, V3 extends T3, V4 extends T4>
    void run(Runnable4<? super V1, ? super V2, ? super V3, ? super V4> r, 
             Runnable4<? super V1, ? super V2, ? super V3, ? super V4> infiniteCase, 
             V1 arg1, V2 arg2, V3 arg3, V4 arg4) {
    
    @SuppressWarnings("unchecked") Runnable4<? super V1, ? super V2, ? super V3, ? super V4> toRun = 
      (Runnable4<? super V1, ? super V2, ? super V3, ? super V4>)
        (contains(arg1, arg2, arg3, arg4) ? infiniteCase : r);
    push(arg1, arg2, arg3, arg4);
    try { toRun.run(arg1, arg2, arg3, arg4); }
    finally { pop(arg1, arg2, arg3, arg4); }
  }
  
  
  public <V1 extends T1, V2 extends T2, V3 extends T3, V4 extends T4>
    void run(Runnable4<? super V1, ? super V2, ? super V3, ? super V4> r, 
             Runnable4<? super V1, ? super V2, ? super V3, ? super V4> infiniteCase, 
             V1 arg1, V2 arg2, V3 arg3, V4 arg4, int threshold) {
    
    @SuppressWarnings("unchecked") Runnable4<? super V1, ? super V2, ? super V3, ? super V4> toRun = 
      (Runnable4<? super V1, ? super V2, ? super V3, ? super V4>)
        (contains(arg1, arg2, arg3, arg4, threshold) ? infiniteCase : r);
    push(arg1, arg2, arg3, arg4);
    try { toRun.run(arg1, arg2, arg3, arg4); }
    finally { pop(arg1, arg2, arg3, arg4); }
  }
  
  
  public <R> R apply(Thunk<? extends R> thunk, R infiniteCase, T1 arg1, T2 arg2, T3 arg3, T4 arg4) {
    if (!contains(arg1, arg2, arg3, arg4)) { 
      push(arg1, arg2, arg3, arg4);
      try { return thunk.value(); }
      finally { pop(arg1, arg2, arg3, arg4); }
    }
    else { return infiniteCase; }
  }
  
  
  public <R> R apply(Thunk<? extends R> thunk, R infiniteCase, T1 arg1, T2 arg2, T3 arg3, T4 arg4, 
                     int threshold) {
    if (!contains(arg1, arg2, arg3, arg4, threshold)) { 
      push(arg1, arg2, arg3, arg4);
      try { return thunk.value(); }
      finally { pop(arg1, arg2, arg3, arg4); }
    }
    else { return infiniteCase; }
  }
  
  
  public <R> R apply(Thunk<? extends R> thunk, Thunk<? extends R> infiniteCase, T1 arg1, 
                     T2 arg2, T3 arg3, T4 arg4) {
    Thunk<? extends R> toApply = (contains(arg1, arg2, arg3, arg4) ? infiniteCase : thunk);
    push(arg1, arg2, arg3, arg4);
    try { return toApply.value(); }
    finally { pop(arg1, arg2, arg3, arg4); }
  }
  
  
  public <R> R apply(Thunk<? extends R> thunk, Thunk<? extends R> infiniteCase, T1 arg1, 
                     T2 arg2, T3 arg3, T4 arg4, int threshold) {
    Thunk<? extends R> toApply = (contains(arg1, arg2, arg3, arg4, threshold) ? infiniteCase : thunk);
    push(arg1, arg2, arg3, arg4);
    try { return toApply.value(); }
    finally { pop(arg1, arg2, arg3, arg4); }
  }
  
  
  public <V1 extends T1, V2 extends T2, V3 extends T3, V4 extends T4, R> 
    R apply(Lambda4<? super V1, ? super V2, ? super V3, ? super V4, ? extends R> lambda, 
            R infiniteCase, V1 arg1, V2 arg2, V3 arg3, V4 arg4) {
    if (!contains(arg1, arg2, arg3, arg4)) { 
      push(arg1, arg2, arg3, arg4);
      try { return lambda.value(arg1, arg2, arg3, arg4); }
      finally { pop(arg1, arg2, arg3, arg4); }
    }
    else { return infiniteCase; }
  }
  
  
  public <V1 extends T1, V2 extends T2, V3 extends T3, V4 extends T4, R> 
    R apply(Lambda4<? super V1, ? super V2, ? super V3, ? super V4, ? extends R> lambda, 
            R infiniteCase, V1 arg1, V2 arg2, V3 arg3, V4 arg4, int threshold) {
    if (!contains(arg1, arg2, arg3, arg4, threshold)) { 
      push(arg1, arg2, arg3, arg4);
      try { return lambda.value(arg1, arg2, arg3, arg4); }
      finally { pop(arg1, arg2, arg3, arg4); }
    }
    else { return infiniteCase; }
  }
  
  
  public <V1 extends T1, V2 extends T2, V3 extends T3, V4 extends T4, R> 
    R apply(Lambda4<? super V1, ? super V2, ? super V3, ? super V4, ? extends R> lambda, 
            Lambda4<? super V1, ? super V2, ? super V3, ? super V4, ? extends R> infiniteCase, 
            V1 arg1, V2 arg2, V3 arg3, V4 arg4) {
    
    @SuppressWarnings("unchecked") Lambda4<? super V1, ? super V2, ? super V3, ? super V4, ? extends R> toApply = 
      (Lambda4<? super V1, ? super V2, ? super V3, ? super V4, ? extends R>) 
        (contains(arg1, arg2, arg3, arg4) ? infiniteCase : lambda);
    push(arg1, arg2, arg3, arg4);
    try { return toApply.value(arg1, arg2, arg3, arg4); }
    finally { pop(arg1, arg2, arg3, arg4); }
  }
  
  
  public <V1 extends T1, V2 extends T2, V3 extends T3, V4 extends T4, R> 
    R apply(Lambda4<? super V1, ? super V2, ? super V3, ? super V4, ? extends R> lambda, 
            Lambda4<? super V1, ? super V2, ? super V3, ? super V4, ? extends R> infiniteCase, 
            V1 arg1, V2 arg2, V3 arg3, V4 arg4, int threshold) {
    
    @SuppressWarnings("unchecked") Lambda4<? super V1, ? super V2, ? super V3, ? super V4, ? extends R> toApply = 
      (Lambda4<? super V1, ? super V2, ? super V3, ? super V4, ? extends R>)
        (contains(arg1, arg2, arg3, arg4, threshold) ? infiniteCase : lambda);
    push(arg1, arg2, arg3, arg4);
    try { return toApply.value(arg1, arg2, arg3, arg4); }
    finally { pop(arg1, arg2, arg3, arg4); }
  }
  
  
  public static <T1, T2, T3, T4> RecursionStack4<T1, T2, T3, T4> make() { 
    return new RecursionStack4<T1, T2, T3, T4>();
  }

  
  public static <T1, T2, T3, T4> RecursionStack4<T1, T2, T3, T4> 
    make(Lambda4<? super T1, ? super T2, ? super T3, ? super T4, ? extends Quad<T1, T2, T3, T4>> quadFactory) {
    return new RecursionStack4<T1, T2, T3, T4>(quadFactory);
  }
  
}
