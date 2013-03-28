

package edu.rice.cs.plt.recur;

import java.util.LinkedList;
import java.util.Map;
import java.util.HashMap;
import edu.rice.cs.plt.tuple.Pair;
import edu.rice.cs.plt.tuple.IdentityPair;
import edu.rice.cs.plt.lambda.Thunk;
import edu.rice.cs.plt.lambda.Lambda2;
import edu.rice.cs.plt.lambda.LambdaUtil;


public class PrecomputedRecursionStack2<T1, T2, R> {
  
  private final Lambda2<? super T1, ? super T2, ? extends Pair<T1, T2>> _pairFactory;
  private final Map<Pair<T1, T2>, Lambda2<? super T1, ? super T2, ? extends R>> _previous;
  private final LinkedList<Pair<T1, T2>> _stack;
  
  
  public PrecomputedRecursionStack2() { this(IdentityPair.<T1, T2>factory()); }
  
  
  public PrecomputedRecursionStack2(Lambda2<? super T1, ? super T2, ? extends Pair<T1, T2>> pairFactory) {
    _pairFactory = pairFactory;
    _previous = new HashMap<Pair<T1, T2>, Lambda2<? super T1, ? super T2, ? extends R>>();
    _stack = new LinkedList<Pair<T1, T2>>();
  }
  
  
  public boolean contains(T1 arg1, T2 arg2) {
    return _previous.containsKey(_pairFactory.value(arg1, arg2));
  }
  
  
  public R get(T1 arg1, T2 arg2) {
    Lambda2<? super T1, ? super T2, ? extends R> result = 
      _previous.get(_pairFactory.value(arg1, arg2));
    if (result == null) { throw new IllegalArgumentException("Values are not on the stack"); }
    return result.value(arg1, arg2);
  }
  
  
  public void push(T1 arg1, T2 arg2, R value) {
    push(arg1, arg2, (Lambda2<Object, Object, R>) LambdaUtil.valueLambda(value));
  }
  
  
  public void push(T1 arg1, T2 arg2, Thunk<? extends R> value) {
    push(arg1, arg2, (Lambda2<Object, Object, ? extends R>) LambdaUtil.promote(value));
  }
  
  
  public void push(T1 arg1, T2 arg2, Lambda2<? super T1, ? super T2, ? extends R> value) {
    Pair<T1, T2> wrapped = _pairFactory.value(arg1, arg2);
    if (_previous.containsKey(wrapped)) {
      throw new IllegalArgumentException("The given arguments are already on the stack");
    }
    _stack.addLast(wrapped);
    _previous.put(wrapped, value);
  }
  
  
  public void pop(T1 arg1, T2 arg2) {
    Pair<T1, T2> wrapped = _pairFactory.value(arg1, arg2);
    if (_stack.isEmpty() || !_stack.getLast().equals(wrapped)) {
      throw new IllegalArgumentException("the given arguments are not on top of the stack");
    }
    _stack.removeLast();
    _previous.remove(wrapped);
  }
  
  
  public int size() { return _stack.size(); }
  
  
  public boolean isEmpty() { return _stack.isEmpty(); }
  
  
  public R apply(Thunk<? extends R> thunk, R precomputed, T1 arg1, T2 arg2) {
    if (!contains(arg1, arg2)) { 
      push(arg1, arg2, precomputed);
      try { return thunk.value(); }
      finally { pop(arg1, arg2); }
    }
    else { return get(arg1, arg2); }
  }
  
  
  public R apply(Thunk<? extends R> thunk, Thunk<? extends R> precomputed, T1 arg1, T2 arg2) {
    if (!contains(arg1, arg2)) { 
      push(arg1, arg2, precomputed);
      try { return thunk.value(); }
      finally { pop(arg1, arg2); }
    }
    else { return get(arg1, arg2); }
  }
  
  
  public R apply(Thunk<? extends R> thunk, Lambda2<? super T1, ? super T2, ? extends R> precomputed, 
                 T1 arg1, T2 arg2) {
    if (!contains(arg1, arg2)) { 
      push(arg1, arg2, precomputed);
      try { return thunk.value(); }
      finally { pop(arg1, arg2); }
    }
    else { return get(arg1, arg2); }
  }
  
  
  public R apply(Lambda2<? super T1, ? super T2, ? extends R> lambda, R precomputed, T1 arg1, T2 arg2) {
    if (!contains(arg1, arg2)) { 
      push(arg1, arg2, precomputed);
      try { return lambda.value(arg1, arg2); }
      finally { pop(arg1, arg2); }
    }
    else { return get(arg1, arg2); }
  }
  
  
  public R apply(Lambda2<? super T1, ? super T2, ? extends R> lambda, Thunk<? extends R> precomputed, 
                 T1 arg1, T2 arg2) {
    if (!contains(arg1, arg2)) { 
      push(arg1, arg2, precomputed);
      try { return lambda.value(arg1, arg2); }
      finally { pop(arg1, arg2); }
    }
    else { return get(arg1, arg2); }
  }
  
  
  public R apply(Lambda2<? super T1, ? super T2, ? extends R> lambda, 
                 Lambda2<? super T1, ? super T2, ? extends R> precomputed, 
                 T1 arg1, T2 arg2) {
    if (!contains(arg1, arg2)) { 
      push(arg1, arg2, precomputed);
      try { return lambda.value(arg1, arg2); }
      finally { pop(arg1, arg2); }
    }
    else { return get(arg1, arg2); }
  }
    
  
  public static <T1, T2, R> PrecomputedRecursionStack2<T1, T2, R> make() {
    return new PrecomputedRecursionStack2<T1, T2, R>();
  }
  
  
  public static <T1, T2, R> PrecomputedRecursionStack2<T1, T2, R> 
    make(Lambda2<? super T1, ? super T2, ? extends Pair<T1, T2>> pairFactory) {
    return new PrecomputedRecursionStack2<T1, T2, R>(pairFactory);
  }
  
}
