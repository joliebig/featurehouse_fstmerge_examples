

package edu.rice.cs.plt.recur;

import java.util.LinkedList;
import java.util.Map;
import java.util.HashMap;
import edu.rice.cs.plt.tuple.Wrapper;
import edu.rice.cs.plt.tuple.IdentityWrapper;
import edu.rice.cs.plt.lambda.Thunk;
import edu.rice.cs.plt.lambda.Lambda;
import edu.rice.cs.plt.lambda.LambdaUtil;


public class PrecomputedRecursionStack<T, R> {
  
  private final Lambda<? super T, ? extends Wrapper<T>> _wrapperFactory;
  private final Map<Wrapper<T>, Lambda<? super T, ? extends R>> _previous;
  private final LinkedList<Wrapper<T>> _stack;
  
  
  public PrecomputedRecursionStack() { this(IdentityWrapper.<T>factory()); }
  
  
  public PrecomputedRecursionStack(Lambda<? super T, ? extends Wrapper<T>> wrapperFactory) {
    _wrapperFactory = wrapperFactory;
    _previous = new HashMap<Wrapper<T>, Lambda<? super T, ? extends R>>();
    _stack = new LinkedList<Wrapper<T>>();
  }
  
  
  public boolean contains(T arg) { return _previous.containsKey(_wrapperFactory.value(arg)); }
  
  
  public R get(T arg) {
    Lambda<? super T, ? extends R> result = _previous.get(_wrapperFactory.value(arg));
    if (result == null) { throw new IllegalArgumentException("Value is not on the stack"); }
    return result.value(arg);
  }
  
  
  public void push(T arg, R value) { push(arg, (Lambda<Object, R>) LambdaUtil.valueLambda(value)); }
  
  
  public void push(T arg, Thunk<? extends R> value) {
    push(arg, (Lambda<Object, ? extends R>) LambdaUtil.promote(value));
  }
  
  
  public void push(T arg, Lambda<? super T, ? extends R> value) {
    Wrapper<T> wrapped = _wrapperFactory.value(arg);
    if (_previous.containsKey(wrapped)) {
      throw new IllegalArgumentException("arg is already on the stack");
    }
    _stack.addLast(wrapped);
    _previous.put(wrapped, value);
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
  
  
  public R apply(Thunk<? extends R> thunk, R precomputed, T arg) {
    if (!contains(arg)) { 
      push(arg, precomputed);
      try { return thunk.value(); }
      finally { pop(arg); }
    }
    else { return get(arg); }
  }
  
  
  public R apply(Thunk<? extends R> thunk, Thunk<? extends R> precomputed, T arg) {
    if (!contains(arg)) { 
      push(arg, precomputed);
      try { return thunk.value(); }
      finally { pop(arg); }
    }
    else { return get(arg); }
  }
  
  
  public R apply(Thunk<? extends R> thunk, Lambda<? super T, ? extends R> precomputed, T arg) {
    if (!contains(arg)) { 
      push(arg, precomputed);
      try { return thunk.value(); }
      finally { pop(arg); }
    }
    else { return get(arg); }
  }
  
  
  public R apply(Lambda<? super T, ? extends R> lambda, R precomputed, T arg) {
    if (!contains(arg)) { 
      push(arg, precomputed);
      try { return lambda.value(arg); }
      finally { pop(arg); }
    }
    else { return get(arg); }
  }
  
  
  public R apply(Lambda<? super T, ? extends R> lambda, Thunk<? extends R> precomputed, T arg) {
    if (!contains(arg)) { 
      push(arg, precomputed);
      try { return lambda.value(arg); }
      finally { pop(arg); }
    }
    else { return get(arg); }
  }
  
  
  public R apply(Lambda<? super T, ? extends R> lambda, Lambda<? super T, ? extends R> precomputed, 
                     T arg) {
    if (!contains(arg)) { 
      push(arg, precomputed);
      try { return lambda.value(arg); }
      finally { pop(arg); }
    }
    else { return get(arg); }
  }
    
  
  public static <T, R> PrecomputedRecursionStack<T, R> make() {
    return new PrecomputedRecursionStack<T, R>();
  }
  
  
  public static <T, R> PrecomputedRecursionStack<T, R> 
    make(Lambda<? super T, ? extends Wrapper<T>> wrapperFactory) {
    return new PrecomputedRecursionStack<T, R>(wrapperFactory);
  }
  
}
