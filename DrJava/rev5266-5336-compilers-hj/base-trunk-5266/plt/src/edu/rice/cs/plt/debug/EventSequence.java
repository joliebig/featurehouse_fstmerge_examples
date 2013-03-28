

package edu.rice.cs.plt.debug;

import java.util.List;
import java.util.LinkedList;
import java.util.Arrays;
import java.util.Iterator;
import edu.rice.cs.plt.collect.CollectUtil;
import edu.rice.cs.plt.iter.AbstractIterable;
import edu.rice.cs.plt.iter.SizedIterable;
import edu.rice.cs.plt.iter.IterUtil;
import edu.rice.cs.plt.iter.ImmutableIterator;
import edu.rice.cs.plt.tuple.Option;
import edu.rice.cs.plt.tuple.Pair;


public class EventSequence<T> extends AbstractIterable<T> implements SizedIterable<T> {
  
  private final List<T> _events; 
  
  public EventSequence() {
    
    
    
    _events = new LinkedList<T>();
  }
  
  public boolean isEmpty() { return false; }
  public int size() { return _events.size(); }
  public int size(int bound) { return IterUtil.sizeOf(_events, bound); }
  public boolean isInfinite() { return false; }
  public boolean hasFixedSize() { return false; }
  public boolean isStatic() { return false; }
  
  public Iterator<T> iterator() { return new ImmutableIterator<T>(_events.iterator()); }
  
  
  public void record(T... events) { record(Arrays.asList(events)); }
  
  
  public void record(Iterable<? extends T> events) {
    synchronized (_events) {
      _events.addAll(CollectUtil.asCollection(events));
    }
  }
  
  
  public void assertEmpty() {
    synchronized (_events) {
      if (!_events.isEmpty()) { throw new AssertionError("Unexpected event: " + _events.get(0)); }
    }
  }
  
  
  public void assertEmpty(String message) {
    synchronized (_events) {
      if (!_events.isEmpty()) { throw new AssertionError(message); }
    }
  }
  
  
  public void assertOccurance(T... expectedEvents) { assertOccurance(Arrays.asList(expectedEvents)); }
  
  
  public void assertOccurance(Iterable<? extends T> expectedEvents) {
    Option<T> missing = checkOccurance(expectedEvents);
    if (missing.isSome()) {
      throw new AssertionError("Event " + missing.unwrap() + " did not occur");
    }
  }
  
  
  public void assertOccurance(String message, Iterable<? extends T> expectedEvents) {
    Option<T> missing = checkOccurance(expectedEvents);
    if (missing.isSome()) {
      throw new AssertionError(message);
    }
  }
  
  
  private Option<T> checkOccurance(Iterable<? extends T> expectedEvents) {
    Option<T> missing = Option.none();
    synchronized (_events) {
      for (T expected : expectedEvents) {
        boolean removed = _events.remove(expected);
        if (!removed && missing.isNone()) { missing = Option.some(expected); }
      }
    }
    return missing;
  }
  
  
  public void assertSequence(T... expectedEvents) { assertSequence(Arrays.asList(expectedEvents)); }
  
  
  public void assertSequence(Iterable<? extends T> expectedEvents) {
    Option<Pair<T, Option<T>>> mismatched = checkSequence(expectedEvents);
    if (mismatched.isSome()) {
      Pair<T, Option<T>> pair = mismatched.unwrap();
      if (pair.second().isSome()) {
        throw new AssertionError("Unexpected event.  Expected: " + pair.first() +
                                 "; Actual: " + pair.second().unwrap());
      }
      else {
        throw new AssertionError("Event " + pair.first() + " did not occur");
      }
    }
  }
  
  
  public void assertSequence(String message, Iterable<? extends T> expectedEvents) {
    Option<?> mismatched = checkSequence(expectedEvents);
    if (mismatched.isSome()) {
      throw new AssertionError(message);
    }
  }
  
  
  private Option<Pair<T, Option<T>>> checkSequence(Iterable<? extends T> expectedEvents) {
    Iterator<? extends T> expected = expectedEvents.iterator();
    synchronized (_events) {
      Iterator<? extends T> actual = _events.iterator();
      while (expected.hasNext() && actual.hasNext()) {
        T exp = expected.next();
        T act = actual.next();
        if (exp == null ? act == null : exp.equals(act)) {
          actual.remove();
        }
        else { return Option.some(Pair.make(exp, Option.some(act))); }
      }
    }
    if (expected.hasNext()) {
      return Option.some(Pair.<T, Option<T>>make(expected.next(), Option.<T>none()));
    }
    return Option.none();
  }
  
  
  public void assertContents(T... expectedEvents) { assertContents(Arrays.asList(expectedEvents)); }
  
  
  public void assertContents(Iterable<? extends T> expectedEvents) {
    assertSequence(expectedEvents);
    if (!_events.isEmpty()) {
      throw new AssertionError("Unexpected additional event: " + _events.get(0));
    }
  }
  
  
  public void assertContents(String message, Iterable<? extends T> expectedEvents) {
    assertSequence(message, expectedEvents);
    if (!_events.isEmpty()) { throw new AssertionError(message); }
  }
  
}
