

package edu.rice.cs.util.sexp;

public class BoolAtom implements Atom {
  public static final BoolAtom TRUE = new BoolAtom(true);
  public static final BoolAtom FALSE = new BoolAtom(false);
  
  private boolean _bool;
  private BoolAtom(boolean bool) { _bool = bool; }
  
  
  public boolean getValue() {
    return _bool;
  }
  
  
  public <Ret> Ret accept(SExpVisitor<Ret> v){
    return v.forBoolAtom(this);
  }
  
  public String toString() { return "" + _bool; }
}