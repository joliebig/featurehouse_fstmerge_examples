

package edu.rice.cs.plt.recur;

import java.util.LinkedList;
import java.util.Map;
import java.util.HashMap;
import edu.rice.cs.plt.tuple.Triple;
import edu.rice.cs.plt.tuple.IdentityTriple;
import edu.rice.cs.plt.lambda.Thunk;
import edu.rice.cs.plt.lambda.Lambda3;
import edu.rice.cs.plt.lambda.LambdaUtil;


public class PrecomputedRecursionStack3<T1, T2, T3, R> {
  
  private final Lambda3<? super T1, ? super T2, ? super T3, ? extends Triple<T1, T2, T3>> _tripleFactory;
  private final Map<Triple<T1, T2, T3>, Lambda3<? super T1, ? super T2, ? super T3, ? extends R>> _previous;
  private final LinkedList<Triple<T1, T2, T3>> _stack;
  
  
  public PrecomputedRecursionStack3() { this(IdentityTriple.<T1, T2, T3>factory()); }
  
  
  public PrecomputedRecursionStack3(Lambda3<? super T1, ? super T2, ? super T3, 
                                            ? extends Triple<T1, T2, T3>> tripleFactory) {
    _tripleFactory = tripleFactory;
    _previous = new HashMap<Triple<T1, T2, T3>, Lambda3<? super T1, ? super T2, ? super T3, ? extends R>>();
    _stack = new LinkedList<Triple<T1, T2, T3>>();
  }
  
  
  public boolean contains(T1 arg1, T2 arg2, T3 arg3) {
    return _previous.containsKey(_tripleFactory.value(arg1, arg2, arg3));
  }
  
  
  public R get(T1 arg1, T2 arg2, T3 arg3) {
    Lambda3<? super T1, ? super T2, ? super T3, ? extends R> result = 
      _previous.get(_tripleFactory.value(arg1, arg2, arg3));
    if (result == null) { throw new IllegalArgumentException("Values are not on the stack"); }
    return result.value(arg1, arg2, arg3);
  }
  
  
  public void push(T1 arg1, T2 arg2, T3 arg3, R value) {
    push(arg1, arg2, arg3, (Lambda3<Object, Object, Object, R>) LambdaUtil.valueLambda(value));
  }
  
  
  public void push(T1 arg1, T2 arg2, T3 arg3, Thunk<? extends R> value) {
    push(arg1, arg2, arg3, (Lambda3<Object, Object, Object, ? extends R>) LambdaUtil.promote(value));
  }
  
  
  public void push(T1 arg1, T2 arg2, T3 arg3, Lambda3<? super T1, ? super T2, ? super T3, ? extends R> value) {
    Triple<T1, T2, T3> wrapped = _tripleFactory.value(arg1, arg2, arg3);
    if (_previous.containsKey(wrapped)) {
      throw new IllegalArgumentException("The given arguments are already on the stack");
    }
    _stack.addLast(wrapped);
    _previous.put(wrapped, value);
  }
  
  
  public void pop(T1 arg1, T2 arg2, T3 arg3) {
    Triple<T1, T2, T3> wrapped = _tripleFactory.value(arg1, arg2, arg3);
    if (_stack.isEmpty() || !_stack.getLast().equals(wrapped)) {
      throw new IllegalArgumentException("the given arguments are not on top of the stack");
    }
    _stack.removeLast();
    _previous.remove(wrapped);
  }
  
  
  public int size() { return _stack.size(); }
  
  
  public boolean isEmpty() { return _stack.isEmpty(); }
  
  
  public R apply(Thunk<? extends R> thunk, R precomputed, T1 arg1, T2 arg2, T3 arg3) {
    if (!contains(arg1, arg2, arg3)) { 
      push(arg1, arg2, arg3, precomputed);
      try { return thunk.value(); }
      finally { pop(arg1, arg2, arg3); }
    }
    else { return get(arg1, arg2, arg3); }
  }
  
  
  public R apply(Thunk<? extends R> thunk, Thunk<? extends R> precomputed, T1 arg1, T2 arg2, T3 arg3) {
    if (!contains(arg1, arg2, arg3)) { 
      push(arg1, arg2, arg3, precomputed);
      try { return thunk.value(); }
      finally { pop(arg1, arg2, arg3); }
    }
    else { return get(arg1, arg2, arg3); }
  }
  
  
  public R apply(Thunk<? extends R> thunk, Lambda3<? super T1, ? super T2, ? super T3, ? extends R> precomputed, 
                 T1 arg1, T2 arg2, T3 arg3) {
    if (!contains(arg1, arg2, arg3)) { 
      push(arg1, arg2, arg3, precomputed);
      try { return thunk.value(); }
      finally { pop(arg1, arg2, arg3); }
    }
    else { return get(arg1, arg2, arg3); }
  }
  
  
  public R apply(Lambda3<? super T1, ? super T2, ? super T3, ? extends R> lambda, R precomputed,
                 T1 arg1, T2 arg2, T3 arg3) {
    if (!contains(arg1, arg2, arg3)) { 
      push(arg1, arg2, arg3, precomputed);
      try { return lambda.value(arg1, arg2, arg3); }
      finally { pop(arg1, arg2, arg3); }
    }
    else { return get(arg1, arg2, arg3); }
  }
  
  
  public R apply(Lambda3<? super T1, ? super T2, ? super T3, ? extends R> lambda, 
                 Thunk<? extends R> precomputed, 
                 T1 arg1, T2 arg2, T3 arg3) {
    if (!contains(arg1, arg2, arg3)) { 
      push(arg1, arg2, arg3, precomputed);
      try { return lambda.value(arg1, arg2, arg3); }
      finally { pop(arg1, arg2, arg3); }
    }
    else { return get(arg1, arg2, arg3); }
  }
  
  
  public R apply(Lambda3<? super T1, ? super T2, ? super T3, ? extends R> lambda, 
                 Lambda3<? super T1, ? super T2, ? super T3, ? extends R> precomputed, 
                 T1 arg1, T2 arg2, T3 arg3) {
    if (!contains(arg1, arg2, arg3)) { 
      push(arg1, arg2, arg3, precomputed);
      try { return lambda.value(arg1, arg2, arg3); }
      finally { pop(arg1, arg2, arg3); }
    }
    else { return get(arg1, arg2, arg3); }
  }
    
  
  public static <T1, T2, T3, R> PrecomputedRecursionStack3<T1, T2, T3, R> make() {
    return new PrecomputedRecursionStack3<T1, T2, T3, R>();
  }
  
  
  public static <T1, T2, T3, R> PrecomputedRecursionStack3<T1, T2, T3, R> 
    make(Lambda3<? super T1, ? super T2, ? super T3, ? extends Triple<T1, T2, T3>> tripleFactory) {
    return new PrecomputedRecursionStack3<T1, T2, T3, R>(tripleFactory);
  }
  
}
