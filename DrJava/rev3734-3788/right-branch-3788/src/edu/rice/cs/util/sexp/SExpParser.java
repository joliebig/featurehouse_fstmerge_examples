

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
      SExpToken t = _lex.readToken();
      if (t == LeftParenToken.ONLY) {
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
      SExpToken t = _lex.readToken();
      assertNotEOF(t);
      if (t == LeftParenToken.ONLY) {
        return parseList();
      }
      else {
        return parseAtom(t);
      }
    }
    
    
    private SEList parseList() {
      LinkedList<SExp> list = new LinkedList<SExp>();
      SExpToken t = _lex.peek();
      assertNotEOF(t);
      
      while (t != RightParenToken.ONLY) {
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
    
    
    private Atom parseAtom(SExpToken t) {
      if (t instanceof BooleanToken) {
        if (((BooleanToken)t).getValue())
          return BoolAtom.TRUE;
        else 
          return BoolAtom.FALSE;
      }
      else if (t instanceof NumberToken) {
        return new NumberAtom(((NumberToken)t).getValue());
      }
      else if (t instanceof QuotedTextToken) {
        return new QuotedTextAtom(t.getText());
      }
      else {
        return new TextAtom(t.getText());
      }
    }
    
    
    private void assertNotEOF(SExpToken t) {
      if (t == null) {
        throw new PrivateParseException("Unexpected <EOF> at line " + _lex.lineno());
      }
    }
  }
  
  
  private static class PrivateParseException extends RuntimeException {
    
    public PrivateParseException(String msg) { super(msg); }
  }
}