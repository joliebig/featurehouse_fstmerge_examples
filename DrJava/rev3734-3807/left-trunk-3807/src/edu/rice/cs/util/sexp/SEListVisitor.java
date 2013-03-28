

package edu.rice.cs.util.sexp;



public interface SEListVisitor<Ret> {
  
  public Ret forEmpty(Empty e);
  
  public Ret forCons(Cons c);
    
}