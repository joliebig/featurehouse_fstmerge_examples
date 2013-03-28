

package edu.rice.cs.util;
import java.io.Serializable;

public class Pair<T,U> implements Serializable{
  T _first;
  U _second;

  public Pair(T first, U second) {
    _first = first;
    _second = second;
  }

  public T getFirst() {
    return _first;
  }

  public U getSecond() {
    return _second;
  }
  
  public String toString() {
    return "("+getFirst().toString()+", "+getSecond().toString()+")"; 
  }
    
}
