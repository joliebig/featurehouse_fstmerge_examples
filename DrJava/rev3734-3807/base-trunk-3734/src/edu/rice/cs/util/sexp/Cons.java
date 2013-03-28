

package edu.rice.cs.util.sexp;

public class Cons extends SEList {
  
  private SExp _first;
  private SEList _rest;
  
  public Cons(SExp first, SEList rest) {
    _first = first;
    _rest = rest;
  }
  
  public SExp getFirst() { return _first; }
  
  public SEList getRest() { return _rest; }
  
  public <Ret> Ret accept(SExpVisitor<Ret> v) {
    return v.forCons(this);
  }
  public <Ret> Ret accept(SEListVisitor<Ret> v) {
    return v.forCons(this);
  }
  
  protected String toStringHelp() {
    return " " + _first + _rest.toStringHelp();
  }
  
  public String toString() {
    return "(" + _first + _rest.toStringHelp();
  }
}