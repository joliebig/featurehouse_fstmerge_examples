

package edu.rice.cs.util.sexp;


public interface Atom extends SExp {
  
  public <Ret> Ret accept(SExpVisitor<Ret> v);
}