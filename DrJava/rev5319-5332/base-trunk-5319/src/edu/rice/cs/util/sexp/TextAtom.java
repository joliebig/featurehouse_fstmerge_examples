

package edu.rice.cs.util.sexp;

public class TextAtom implements Atom {
  protected String _text;
  
  public TextAtom(String text) { _text = text; }
  
  public String getText() { return _text; }
  
  
  public <Ret> Ret accept(SExpVisitor<Ret> v){
    return v.forTextAtom(this);
  }
  
  
  public String toString() { return _text; }  
}


class QuotedTextAtom extends TextAtom {
  
  public QuotedTextAtom(String text) { super(text); }
  
  public String toString() { 
    return edu.rice.cs.util.StringOps.convertToLiteral(_text);
  }
}