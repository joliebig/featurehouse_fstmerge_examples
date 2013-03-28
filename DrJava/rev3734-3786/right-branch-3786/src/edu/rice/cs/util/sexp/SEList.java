

package edu.rice.cs.util.sexp;

public abstract class SEList implements SExp {
  public abstract <Ret> Ret accept(SEListVisitor<Ret> v);
  
  protected abstract String toStringHelp();
}