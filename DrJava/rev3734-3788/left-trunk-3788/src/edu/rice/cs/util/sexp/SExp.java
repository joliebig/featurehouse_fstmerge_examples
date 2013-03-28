

package edu.rice.cs.util.sexp;




public interface SExp {
  
  public <Ret> Ret accept(SExpVisitor<Ret> v);
  
}