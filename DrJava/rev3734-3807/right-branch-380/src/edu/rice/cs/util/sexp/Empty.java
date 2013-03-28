

package edu.rice.cs.util.sexp;

public class Empty extends SEList {
 
  public static final Empty ONLY = new Empty();
  
  private Empty() { }
  
  public <Ret> Ret accept(SExpVisitor<Ret> v) {
    return v.forEmpty(this);
  }
  public <Ret> Ret accept(SEListVisitor<Ret> v) {
    return v.forEmpty(this);
  }
  
  protected String toStringHelp() {
    return ")";
  }
  
  public String toString() {
    return "()";
  }
}