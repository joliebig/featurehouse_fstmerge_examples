

package edu.rice.cs.util.sexp;

import java.io.File;
import java.io.Reader;
import java.io.FileReader;
import java.io.StringReader;
import java.io.IOException;
import java.util.List;
import java.util.ArrayList;
import java.util.LinkedList;


public class SExpParser {
  
  public static List<SEList> parse(File f) throws SExpParseException, IOException{
    return parse(new FileReader(f));
  }
  
  public static List<SEList> parse(String s) throws SExpParseException {
    return parse(new StringReader(s));
  }
  
  public static List<SEList> parse(Reader r) throws SExpParseException {
    try {
      return new ParseHelper(r).parseMultiple();
    }
    catch(LexingException e) {
      throw new SExpParseException(e.getMessage());
    }
    catch(PrivateParseException e) {
      throw new SExpParseException(e.getMessage());
    }
  }
  
  
  private static class ParseHelper {
    
    private Lexer _lex;
    
    public ParseHelper(Reader r) {
      _lex = new Lexer(r);
    }
    
    
    public List<SEList> parseMultiple() {
      ArrayList<SEList> l = new ArrayList<SEList>();
      SEList exp;
      while ( (exp = parseTopLevelExp()) != null) {
        l.add(exp);
      }
      return l;
    }
    
    
    public SEList parseTopLevelExp() {
      Tokens.SExpToken t = _lex.readToken();
      if (t == Tokens.LeftParenToken.ONLY) {
        return parseList();
      }
      else if (t == null) {
        return null;
      }
      else {
        throw new PrivateParseException("A top-level s-expression must be a list. "+
                                        "Invalid start of list: " + t);
      }
    }
    
    
    public SExp parseExp() {
      Tokens.SExpToken t = _lex.readToken();
      assertNotEOF(t);
      if (t == Tokens.LeftParenToken.ONLY) {
        return parseList();
      }
      else {
        return parseAtom(t);
      }
    }
    
    
    private SEList parseList() {
      LinkedList<SExp> list = new LinkedList<SExp>();
      Tokens.SExpToken t = _lex.peek();
      assertNotEOF(t);
      
      while (t != Tokens.RightParenToken.ONLY) {
        list.addFirst(parseExp());
        t = _lex.peek();
      }
      
      
      
      _lex.readToken();
      
      
      SEList cons = Empty.ONLY;
      for (SExp exp : list) {
        cons = new Cons(exp, cons);
      }
      return cons;
    }
    
    
    private Atom parseAtom(Tokens.SExpToken t) {
      if (t instanceof Tokens.BooleanToken) {
        if (((Tokens.BooleanToken)t).getValue())
          return BoolAtom.TRUE;
        else 
          return BoolAtom.FALSE;
      }
      else if (t instanceof Tokens.NumberToken) {
        return new NumberAtom(((Tokens.NumberToken)t).getValue());
      }
      else if (t instanceof Tokens.QuotedTextToken) {
        return new QuotedTextAtom(t.getText());
      }
      else {
        return new TextAtom(t.getText());
      }
    }
    
    
    private void assertNotEOF(Tokens.SExpToken t) {
      if (t == null) {
        throw new PrivateParseException("Unexpected <EOF> at line " + _lex.lineno());
      }
    }
  }
  
  
  private static class PrivateParseException extends RuntimeException {
    
    public PrivateParseException(String msg) { super(msg); }
  }
}