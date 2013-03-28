

package edu.rice.cs.javalanglevels;


public class Triple<T,U,V> {
  T _first;
  U _second;
  V _third;

  public Triple(T first, U second, V third) {
    _first = first;
    _second = second;
    _third = third;
  }

  public T getFirst()  { return _first; }
  public U getSecond() { return _second; }
  public V getThird()  { return _third; }
  
  public boolean equals(Object o) {
    if (o == null || o.getClass() != this.getClass()) return false;
    Triple t = (Triple) o;
    return getFirst().equals(t.getFirst()) && getSecond().equals(t.getSecond()) && getThird().equals(t.getThird());
  }
  
  
  public int hashCode() { return _first.hashCode() ^ _second.hashCode(); }
  
  public String toString() { return "Triple(" + _first + ", " + _second + ", " + _third + ")"; }
}