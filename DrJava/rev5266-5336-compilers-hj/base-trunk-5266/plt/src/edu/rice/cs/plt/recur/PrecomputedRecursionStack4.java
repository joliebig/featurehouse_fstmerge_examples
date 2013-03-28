

package edu.rice.cs.plt.recur;

import java.util.LinkedList;
import java.util.Map;
import java.util.HashMap;
import edu.rice.cs.plt.tuple.Quad;
import edu.rice.cs.plt.tuple.IdentityQuad;
import edu.rice.cs.plt.lambda.Thunk;
import edu.rice.cs.plt.lambda.Lambda4;
import edu.rice.cs.plt.lambda.LambdaUtil;


public class PrecomputedRecursionStack4<T1, T2, T3, T4, R> {
  
  private final Lambda4<? super T1, ? super T2, ? super T3, ? super T4, 
                        ? extends Quad<T1, T2, T3, T4>> _quadFactory;
  private final Map<Quad<T1, T2, T3, T4>, 
                    Lambda4<? super T1, ? super T2, ? super T3, ? super T4, ? extends R>> _previous;
  private final LinkedList<Quad<T1, T2, T3, T4>> _stack;
  
  
  public PrecomputedRecursionStack4() { this(IdentityQuad.<T1, T2, T3, T4>factory()); }
  
  
  public PrecomputedRecursionStack4(Lambda4<? super T1, ? super T2, ? super T3, ? super T4,
                                            ? extends Quad<T1, T2, T3, T4>> quadFactory) {
    _quadFactory = quadFactory;
    _previous = new HashMap<Quad<T1, T2, T3, T4>, 
                            Lambda4<? super T1, ? super T2, ? super T3, ? super T4, ? extends R>>();
    _stack = new LinkedList<Quad<T1, T2, T3, T4>>();
  }
  
  
  public boolean contains(T1 arg1, T2 arg2, T3 arg3, T4 arg4) {
    return _previous.containsKey(_quadFactory.value(arg1, arg2, arg3, arg4));
  }
  
  
  public R get(T1 arg1, T2 arg2, T3 arg3, T4 arg4) {
    Lambda4<? super T1, ? super T2, ? super T3, ? super T4, ? extends R> result = 
      _previous.get(_quadFactory.value(arg1, arg2, arg3, arg4));
    if (result == null) { throw new IllegalArgumentException("Values are not on the stack"); }
    return result.value(arg1, arg2, arg3, arg4);
  }
  
  
  public void push(T1 arg1, T2 arg2, T3 arg3, T4 arg4, R value) {
    push(arg1, arg2, arg3, arg4, (Lambda4<Object, Object, Object, Object, R>) LambdaUtil.valueLambda(value));
  }
  
  
  public void push(T1 arg1, T2 arg2, T3 arg3, T4 arg4, Thunk<? extends R> value) {
    push(arg1, arg2, arg3, arg4, (Lambda4<Object, Object, Object, Object, ? extends R>) LambdaUtil.promote(value));
  }
  
  
  public void push(T1 arg1, T2 arg2, T3 arg3, T4 arg4, 
                   Lambda4<? super T1, ? super T2, ? super T3, ? super T4, ? extends R> value) {
    Quad<T1, T2, T3, T4> wrapped = _quadFactory.value(arg1, arg2, arg3, arg4);
    if (_previous.containsKey(wrapped)) {
      throw new IllegalArgumentException("The given arguments are already on the stack");
    }
    _stack.addLast(wrapped);
    _previous.put(wrapped, value);
  }
  
  
  public void pop(T1 arg1, T2 arg2, T3 arg3, T4 arg4) {
    Quad<T1, T2, T3, T4> wrapped = _quadFactory.value(arg1, arg2, arg3, arg4);
    if (_stack.isEmpty() || !_stack.getLast().equals(wrapped)) {
      throw new IllegalArgumentException("the given arguments are not on top of the stack");
    }
    _stack.removeLast();
    _previous.remove(wrapped);
  }
  
  
  public int size() { return _stack.size(); }
  
  
  public boolean isEmpty() { return _stack.isEmpty(); }
  
  
  public R apply(Thunk<? extends R> thunk, R precomputed, T1 arg1, T2 arg2, T3 arg3, T4 arg4) {
    if (!contains(arg1, arg2, arg3, arg4)) { 
      push(arg1, arg2, arg3, arg4, precomputed);
      try { return thunk.value(); }
      finally { pop(arg1, arg2, arg3, arg4); }
    }
    else { return get(arg1, arg2, arg3, arg4); }
  }
  
  
  public R apply(Thunk<? extends R> thunk, Thunk<? extends R> precomputed, 
                 T1 arg1, T2 arg2, T3 arg3, T4 arg4) {
    if (!contains(arg1, arg2, arg3, arg4)) { 
      push(arg1, arg2, arg3, arg4, precomputed);
      try { return thunk.value(); }
      finally { pop(arg1, arg2, arg3, arg4); }
    }
    else { return get(arg1, arg2, arg3, arg4); }
  }
  
  
  public R apply(Thunk<? extends R> thunk, 
                 Lambda4<? super T1, ? super T2, ? super T3, ? super T4, ? extends R> precomputed, 
                 T1 arg1, T2 arg2, T3 arg3, T4 arg4) {
    if (!contains(arg1, arg2, arg3, arg4)) { 
      push(arg1, arg2, arg3, arg4, precomputed);
      try { return thunk.value(); }
      finally { pop(arg1, arg2, arg3, arg4); }
    }
    else { return get(arg1, arg2, arg3, arg4); }
  }
  
  
  public R apply(Lambda4<? super T1, ? super T2, ? super T3, ? super T4, ? extends R> lambda, R precomputed,
                 T1 arg1, T2 arg2, T3 arg3, T4 arg4) {
    if (!contains(arg1, arg2, arg3, arg4)) { 
      push(arg1, arg2, arg3, arg4, precomputed);
      try { return lambda.value(arg1, arg2, arg3, arg4); }
      finally { pop(arg1, arg2, arg3, arg4); }
    }
    else { return get(arg1, arg2, arg3, arg4); }
  }
  
  
  public R apply(Lambda4<? super T1, ? super T2, ? super T3, ? super T4, ? extends R> lambda, 
                 Thunk<? extends R> precomputed, 
                 T1 arg1, T2 arg2, T3 arg3, T4 arg4) {
    if (!contains(arg1, arg2, arg3, arg4)) { 
      push(arg1, arg2, arg3, arg4, precomputed);
      try { return lambda.value(arg1, arg2, arg3, arg4); }
      finally { pop(arg1, arg2, arg3, arg4); }
    }
    else { return get(arg1, arg2, arg3, arg4); }
  }
  
  
  public R apply(Lambda4<? super T1, ? super T2, ? super T3, ? super T4, ? extends R> lambda, 
                 Lambda4<? super T1, ? super T2, ? super T3, ? super T4, ? extends R> precomputed, 
                 T1 arg1, T2 arg2, T3 arg3, T4 arg4) {
    if (!contains(arg1, arg2, arg3, arg4)) { 
      push(arg1, arg2, arg3, arg4, precomputed);
      try { return lambda.value(arg1, arg2, arg3, arg4); }
      finally { pop(arg1, arg2, arg3, arg4); }
    }
    else { return get(arg1, arg2, arg3, arg4); }
  }
    
  
  public static <T1, T2, T3, T4, R> PrecomputedRecursionStack4<T1, T2, T3, T4, R> make() {
    return new PrecomputedRecursionStack4<T1, T2, T3, T4, R>();
  }
  
  
  public static <T1, T2, T3, T4, R> PrecomputedRecursionStack4<T1, T2, T3, T4, R> 
    make(Lambda4<? super T1, ? super T2, ? super T3, ? super T4, ? extends Quad<T1, T2, T3, T4>> quadFactory) {
    return new PrecomputedRecursionStack4<T1, T2, T3, T4, R>(quadFactory);
  }
  
}
