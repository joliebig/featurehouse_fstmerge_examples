

package edu.rice.cs.plt.recur;

import java.util.LinkedList;
import edu.rice.cs.plt.collect.Multiset;
import edu.rice.cs.plt.collect.HashMultiset;
import edu.rice.cs.plt.tuple.Triple;
import edu.rice.cs.plt.tuple.IdentityTriple;
import edu.rice.cs.plt.lambda.Runnable3;
import edu.rice.cs.plt.lambda.Thunk;
import edu.rice.cs.plt.lambda.Lambda3;


public class RecursionStack3<T1, T2, T3> {
  
  private final Lambda3<? super T1, ? super T2, ? super T3, ? extends Triple<T1, T2, T3>> _tripleFactory;
  private final Multiset<Triple<T1, T2, T3>> _previous;
  private final LinkedList<Triple<T1, T2, T3>> _stack;
  
  
  public RecursionStack3() { this(IdentityTriple.<T1, T2, T3>factory()); }
  
  
  public RecursionStack3(Lambda3<? super T1, ? super T2, ? super T3, 
                                 ? extends Triple<T1, T2, T3>> tripleFactory) {
    _tripleFactory = tripleFactory;
    _previous = new HashMultiset<Triple<T1, T2, T3>>();
    _stack = new LinkedList<Triple<T1, T2, T3>>();
  }
  
  
  public boolean contains(T1 arg1, T2 arg2, T3 arg3) {
    return _previous.contains(_tripleFactory.value(arg1, arg2, arg3));
  }
  
  
  public boolean contains(T1 arg1, T2 arg2, T3 arg3, int threshold) {
    return _previous.count(_tripleFactory.value(arg1, arg2, arg3)) >= threshold;
  }
  
  
  public void push(T1 arg1, T2 arg2, T3 arg3) {
    Triple<T1, T2, T3> wrapped = _tripleFactory.value(arg1, arg2, arg3);
    _stack.addLast(wrapped);
    _previous.add(wrapped);
  }
  
  
  public void pop(T1 arg1, T2 arg2, T3 arg3) {
    Triple<T1, T2, T3> wrapped = _tripleFactory.value(arg1, arg2, arg3);
    if (_stack.isEmpty() || !_stack.getLast().equals(wrapped)) {
      throw new IllegalArgumentException("given args are not on top of the stack");
    }
    _stack.removeLast();
    _previous.remove(wrapped);
  }
  
  
  public int size() { return _stack.size(); }
  
  
  public boolean isEmpty() { return _stack.isEmpty(); }
  
  
  public void run(Runnable r, T1 arg1, T2 arg2, T3 arg3) {
    if (!contains(arg1, arg2, arg3)) { 
      push(arg1, arg2, arg3);
      try { r.run(); }
      finally { pop(arg1, arg2, arg3); }
    }
  }
  
  
  public void run(Runnable r, T1 arg1, T2 arg2, T3 arg3, int threshold) {
    if (!contains(arg1, arg2, arg3, threshold)) { 
      push(arg1, arg2, arg3);
      try { r.run(); }
      finally { pop(arg1, arg2, arg3); }
    }
  }
  
  
  public void run(Runnable r, Runnable infiniteCase, T1 arg1, T2 arg2, T3 arg3) {
    Runnable toRun = (contains(arg1, arg2, arg3) ? infiniteCase : r);
    push(arg1, arg2, arg3);
    try { toRun.run(); }
    finally { pop(arg1, arg2, arg3); }
  }
  
  
  public void run(Runnable r, Runnable infiniteCase, T1 arg1, T2 arg2, T3 arg3, int threshold) {
    Runnable toRun = (contains(arg1, arg2, arg3, threshold) ? infiniteCase : r);
    push(arg1, arg2, arg3);
    try { toRun.run(); }
    finally { pop(arg1, arg2, arg3); }
  }
  
  
  public <V1 extends T1, V2 extends T2, V3 extends T3>
    void run(Runnable3<? super V1, ? super V2, ? super V3> r, V1 arg1, V2 arg2, V3 arg3) {
    if (!contains(arg1, arg2, arg3)) { 
      push(arg1, arg2, arg3);
      try { r.run(arg1, arg2, arg3); }
      finally { pop(arg1, arg2, arg3); }
    }
  }
  
  
  public <V1 extends T1, V2 extends T2, V3 extends T3>
    void run(Runnable3<? super V1, ? super V2, ? super V3> r, V1 arg1, V2 arg2, V3 arg3, 
             int threshold) {
    if (!contains(arg1, arg2, arg3, threshold)) { 
      push(arg1, arg2, arg3);
      try { r.run(arg1, arg2, arg3); }
      finally { pop(arg1, arg2, arg3); }
    }
  }
  
  
  public <V1 extends T1, V2 extends T2, V3 extends T3>
    void run(Runnable3<? super V1, ? super V2, ? super V3> r, 
             Runnable3<? super V1, ? super V2, ? super V3> infiniteCase, 
             V1 arg1, V2 arg2, V3 arg3) {
    
    @SuppressWarnings("unchecked") Runnable3<? super V1, ? super V2, ? super V3> toRun = 
      (Runnable3<? super V1, ? super V2, ? super V3>) (contains(arg1, arg2, arg3) ? infiniteCase : r);
    push(arg1, arg2, arg3);
    try { toRun.run(arg1, arg2, arg3); }
    finally { pop(arg1, arg2, arg3); }
  }
  
  
  public <V1 extends T1, V2 extends T2, V3 extends T3>
    void run(Runnable3<? super V1, ? super V2, ? super V3> r, 
             Runnable3<? super V1, ? super V2, ? super V3> infiniteCase, 
                  V1 arg1, V2 arg2, V3 arg3, int threshold) {
    
    @SuppressWarnings("unchecked") Runnable3<? super V1, ? super V2, ? super V3> toRun = 
      (Runnable3<? super V1, ? super V2, ? super V3>) (contains(arg1, arg2, arg3, threshold) ? 
                                                        infiniteCase : r);
    push(arg1, arg2, arg3);
    try { toRun.run(arg1, arg2, arg3); }
    finally { pop(arg1, arg2, arg3); }
  }
  
  
  public <R> R apply(Thunk<? extends R> thunk, R infiniteCase, T1 arg1, T2 arg2, T3 arg3) {
    if (!contains(arg1, arg2, arg3)) { 
      push(arg1, arg2, arg3);
      try { return thunk.value(); }
      finally { pop(arg1, arg2, arg3); }
    }
    else { return infiniteCase; }
  }
  
  
  public <R> R apply(Thunk<? extends R> thunk, R infiniteCase, T1 arg1, T2 arg2, T3 arg3, 
                     int threshold) {
    if (!contains(arg1, arg2, arg3, threshold)) { 
      push(arg1, arg2, arg3);
      try { return thunk.value(); }
      finally { pop(arg1, arg2, arg3); }
    }
    else { return infiniteCase; }
  }
  
  
  public <R> R apply(Thunk<? extends R> thunk, Thunk<? extends R> infiniteCase, T1 arg1, 
                     T2 arg2, T3 arg3) {
    Thunk<? extends R> toApply = (contains(arg1, arg2, arg3) ? infiniteCase : thunk);
    push(arg1, arg2, arg3);
    try { return toApply.value(); }
    finally { pop(arg1, arg2, arg3); }
  }
  
  
  public <R> R apply(Thunk<? extends R> thunk, Thunk<? extends R> infiniteCase, T1 arg1, 
                     T2 arg2, T3 arg3, int threshold) {
    Thunk<? extends R> toApply = (contains(arg1, arg2, arg3, threshold) ? infiniteCase : thunk);
    push(arg1, arg2, arg3);
    try { return toApply.value(); }
    finally { pop(arg1, arg2, arg3); }
  }
  
  
  public <V1 extends T1, V2 extends T2, V3 extends T3,R> 
    R apply(Lambda3<? super V1, ? super V2, ? super V3, ? extends R> lambda, 
            R infiniteCase, V1 arg1, V2 arg2, V3 arg3) {
    if (!contains(arg1, arg2, arg3)) { 
      push(arg1, arg2, arg3);
      try { return lambda.value(arg1, arg2, arg3); }
      finally { pop(arg1, arg2, arg3); }
    }
    else { return infiniteCase; }
  }
  
  
  public <V1 extends T1, V2 extends T2, V3 extends T3,R> 
    R apply(Lambda3<? super V1, ? super V2, ? super V3, ? extends R> lambda, 
            R infiniteCase, V1 arg1, V2 arg2, V3 arg3, int threshold) {
    if (!contains(arg1, arg2, arg3, threshold)) { 
      push(arg1, arg2, arg3);
      try { return lambda.value(arg1, arg2, arg3); }
      finally { pop(arg1, arg2, arg3); }
    }
    else { return infiniteCase; }
  }
  
  
  public <V1 extends T1, V2 extends T2, V3 extends T3,R> 
    R apply(Lambda3<? super V1, ? super V2, ? super V3, ? extends R> lambda, 
            Lambda3<? super V1, ? super V2, ? super V3, ? extends R> infiniteCase, V1 arg1, 
            V2 arg2, V3 arg3) {
    
    @SuppressWarnings("unchecked") Lambda3<? super V1, ? super V2, ? super V3, ? extends R> toApply = 
      (Lambda3<? super V1, ? super V2, ? super V3, ? extends R>) (contains(arg1, arg2, arg3) ? 
                                                                    infiniteCase : lambda);
    push(arg1, arg2, arg3);
    try { return toApply.value(arg1, arg2, arg3); }
    finally { pop(arg1, arg2, arg3); }
  }
  
  
  public <V1 extends T1, V2 extends T2, V3 extends T3,R> 
    R apply(Lambda3<? super V1, ? super V2, ? super V3, ? extends R> lambda, 
            Lambda3<? super V1, ? super V2, ? super V3, ? extends R> infiniteCase, V1 arg1, 
            V2 arg2, V3 arg3, int threshold) {
    
    @SuppressWarnings("unchecked") Lambda3<? super V1, ? super V2, ? super V3, ? extends R> toApply = 
      (Lambda3<? super V1, ? super V2, ? super V3, ? extends R>)
        (contains(arg1, arg2, arg3, threshold) ? infiniteCase : lambda);
    push(arg1, arg2, arg3);
    try { return toApply.value(arg1, arg2, arg3); }
    finally { pop(arg1, arg2, arg3); }
  }

  
  public static <T1, T2, T3> RecursionStack3<T1, T2, T3> make() { 
    return new RecursionStack3<T1, T2, T3>();
  }

  
  public static <T1, T2, T3> RecursionStack3<T1, T2, T3> 
    make(Lambda3<? super T1, ? super T2, ? super T3, ? extends Triple<T1, T2, T3>> tripleFactory) {
    return new RecursionStack3<T1, T2, T3>(tripleFactory);
  }
  
}
