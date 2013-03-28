

package edu.rice.cs.util.sexp;

public class NumberAtom implements Atom {
  private double _num;
  private boolean _hasDecimals;
  public NumberAtom(int num){ 
    _num = (double)num;
    _hasDecimals = false;
  }
  public NumberAtom(double num){
    _num = num;
    _hasDecimals = (num % 1 < 1e-12);
  }
  public boolean hasDecimals() { return _hasDecimals; }
  public int intValue() { return (int)_num; }
  public double doubleValue() { return _num; }
  
  
  public <Ret> Ret accept(SExpVisitor<Ret> v){
    return v.forNumberAtom(this);
  }
  
  public String toString(){ 
    if (_hasDecimals) {
      return "" + doubleValue();
    }
    else {
      return "" + intValue();
    }
  }
}