

package edu.rice.cs.javalanglevels;


public class Pair<T,U> {
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
  
  public boolean equals(Object o) {
    return  (o != null) && (o.getClass() == this.getClass()) &&
      getFirst().equals(((Pair) o).getFirst()) && getSecond().equals(((Pair) o).getSecond());
  }
  
  
  public int hashCode() {
    return _first.hashCode() ^ _second.hashCode();
  }
  
  public String toString() {
    return "Pair(" + _first + ", " + _second + ")";
  }
}