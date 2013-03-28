

package edu.rice.cs.util.sexp;


public interface Tokens {
  
  
   class SExpToken {
    protected String _rep;
    
    
    public SExpToken(String rep) { _rep = rep.intern(); }  
    
    
    public String getText() { return _rep; }
    
    public boolean equals(Object o) {
      return (o != null && o.getClass() == getClass() && ((SExpToken)o)._rep == _rep);
    }
    
    public int hashCode() { return _rep.hashCode(); }
    
    public String toString() { return _rep; }
  }
  
  
  
  class LeftParenToken extends SExpToken {
    public static final LeftParenToken ONLY = new LeftParenToken();
    private LeftParenToken(){ super("("); }
  }
  
  class RightParenToken extends SExpToken {
    public static final RightParenToken ONLY = new RightParenToken();
    private RightParenToken(){ super(")"); }
  }
  
  class BackSlashToken extends SExpToken {
    public static final BackSlashToken ONLY = new BackSlashToken();
    private BackSlashToken(){ super("\\"); }
  }
  
  
  
  
  class WordToken extends SExpToken { public WordToken(String word) { super(word); } }
  
  
  class QuotedTextToken extends SExpToken {
    
    public QuotedTextToken(String txt) { super(txt); }
    public String getFullText() { return "\"" + _rep + "\""; }
  }
  
  
  class BooleanToken extends SExpToken {
    public static final BooleanToken TRUE = new BooleanToken(true);
    public static final BooleanToken FALSE = new BooleanToken(false);
    
    private boolean _bool;
    private BooleanToken(boolean bool){
      super(""+bool);
      _bool = bool;
    }
    public boolean getValue() { return _bool; }
  }
  
  
  class NumberToken extends SExpToken {
    private double _num;
    public NumberToken(double num){
      
      
      super((num % 1 == 0) ? "" + (int)num : "" + num);
      _num = num;
    }
    public double getValue() { return _num; }
  }
  
}
