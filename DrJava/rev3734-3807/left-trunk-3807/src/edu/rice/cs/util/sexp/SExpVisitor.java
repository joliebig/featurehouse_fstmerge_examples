

package edu.rice.cs.util.sexp;


public interface SExpVisitor<Ret> {
  
  public Ret forEmpty(Empty e);
  
  public Ret forCons(Cons c);
  
  public Ret forBoolAtom(BoolAtom b);
  
  public Ret forNumberAtom(NumberAtom n);
  
  public Ret forTextAtom(TextAtom t);
  
}