

package edu.rice.cs.util;

import java.io.Reader;
import java.io.IOException;
import java.util.Stack;
import java.util.HashMap;
import java.util.Set;
import java.util.SortedSet;
import java.util.HashSet;
import java.util.TreeSet;
import java.util.Iterator;
import java.util.ArrayList;

import edu.rice.cs.plt.lambda.Lambda;



public class BalancingStreamTokenizer {
  
  protected Reader _reader;

  
  public Stack<Integer> _pushed = new Stack<Integer>();
  
  
  public static class State {
    
    public HashMap<String,String> quotePairs = new HashMap<String,String>();
    
    
    public TreeSet<String> quotes = new TreeSet<String>();

    
    public TreeSet<String> quoteEnds = new TreeSet<String>();

    
    public TreeSet<String> keywords = new TreeSet<String>();

    
    public HashSet<Integer> whitespace = new HashSet<Integer>();
    
    
    public State() { }
    
    
    public State(State o) {
      quotePairs = new HashMap<String,String>(o.quotePairs);
      keywords = new TreeSet<String>(o.keywords);
      quotes = new TreeSet<String>(o.quotes);
      quoteEnds = new TreeSet<String>(o.quoteEnds);
      whitespace = new HashSet<Integer>(o.whitespace);
    }
  }
    
  
  protected State _state = new State();

  
  protected Stack<State> _stateStack = new Stack<State>();
  
  
  protected Character _escape = null;
  
  
  protected boolean _wasEscape = false;

  
  protected boolean _isEscape = false;

  
  public enum Token { NONE, NORMAL, QUOTED, KEYWORD, END }

  public volatile Token _token = Token.NONE;
  
  
  public BalancingStreamTokenizer(Reader r) {
    this(r,null);
  }
  
  
  public BalancingStreamTokenizer(Reader r, Character escape) {
    _escape = escape;
    _reader = r;
  }
  
  
  public void defaultWhitespaceSetup() {
    wordRange(0,255);
    whitespaceRange(0,32);
  }
  
  
  public void defaultTwoQuoteSetup() {
    wordRange(0,255);
    whitespaceRange(0,32);
    addQuotes("\"", "\"");
    addQuotes("'", "'");
  }

  
  public void defaultThreeQuoteSetup() {
    wordRange(0,255);
    whitespaceRange(0,32);
    addQuotes("\"", "\"");
    addQuotes("'", "'");
    addQuotes("`", "`");
  }
  
  
  public void defaultTwoQuoteCurlySetup() {
    wordRange(0,255);
    whitespaceRange(0,32);
    addQuotes("\"", "\"");
    addQuotes("'", "'");
    addQuotes("{", "}");
  }
  
  
  public void defaultThreeQuoteCurlySetup() {
    wordRange(0,255);
    whitespaceRange(0,32);
    addQuotes("\"", "\"");
    addQuotes("'", "'");
    addQuotes("`", "`");
    addQuotes("{", "}");
  }
  
  
  public void defaultThreeQuoteDollarCurlySetup() {
    wordRange(0,255);
    whitespaceRange(0,32);
    addQuotes("\"", "\"");
    addQuotes("'", "'");
    addQuotes("`", "`");
    addQuotes("${", "}");
  }
  
  
  protected int nextToken() throws IOException {
    if (_pushed.empty()) {
      return _reader.read();
    }
    else {
      return _pushed.pop();
    }
  }
  
  
  protected void pushToken(int token) {
    _pushed.push(token);
  }
  
  
  public State getState() { return new State(_state); }
  
  
  public void setState(State state) { _state = state; }
  
  
  protected void pushState() { _stateStack.push(_state); }
  
  
  protected void popState() { setState(_stateStack.pop()); }

  
  public Token token() { return _token; }
  
  
  public void wordRange(int lo, int hi) {
    ArrayList<String> kwToRemove = new ArrayList<String>();
    ArrayList<String> qpToRemove = new ArrayList<String>();
    for(int i = lo; i <= hi; ++i) {
      
      if (_state.whitespace.contains(i)) {
        _state.whitespace.remove(i);
      }
      
      
      Iterator<String> kit = _state.keywords.iterator();
      while(kit.hasNext()) {
        String s = kit.next();
        if (s.charAt(0) == i) { kwToRemove.add(s); }
      }

      
      Iterator<String> qit = _state.quotes.iterator();
      while(qit.hasNext()) {
        String s = qit.next();
        if (s.charAt(0) == i) { qpToRemove.add(s); }
      }
    }
    
    for(String s: kwToRemove) { _state.keywords.remove(s); }
    for(String s: qpToRemove) {
      _state.quotes.remove(s);
      _state.quoteEnds.remove(_state.quotePairs.get(s));
      _state.quotePairs.remove(s);
    }
  }

  
  public void wordChars(int... c) {
    ArrayList<String> kwToRemove = new ArrayList<String>();
    ArrayList<String> qpToRemove = new ArrayList<String>();
    for(int i: c) {
      
      if (_state.whitespace.contains(i)) {
        _state.whitespace.remove(i);
      }
      
      
      Iterator<String> kit = _state.keywords.iterator();
      while(kit.hasNext()) {
        String s = kit.next();
        if (s.charAt(0) == i) { kwToRemove.add(s); }
      }
      
      
      Iterator<String> qit = _state.quotes.iterator();
      while(qit.hasNext()) {
        String s = qit.next();
        if (s.charAt(0) == i) { qpToRemove.add(s); }
      }
    }
    
    for(String s: kwToRemove) { _state.keywords.remove(s); }
    for(String s: qpToRemove) {
      _state.quotes.remove(s);
      _state.quoteEnds.remove(_state.quotePairs.get(s));
      _state.quotePairs.remove(s);
    }
  }
  
  
  public void whitespaceRange(int lo, int hi) {
    ArrayList<String> kwToRemove = new ArrayList<String>();
    ArrayList<String> qpToRemove = new ArrayList<String>();
    for(int i = lo; i <= hi; ++i) {
      if ((_escape != null) && (i == _escape)) { continue; }

      
      _state.whitespace.add(i);
      
      
      Iterator<String> kit = _state.keywords.iterator();
      while(kit.hasNext()) {
        String s = kit.next();
        if (s.charAt(0) == i) { kwToRemove.add(s); }
      }

      
      Iterator<String> qit = _state.quotes.iterator();
      while(qit.hasNext()) {
        String s = qit.next();
        if (s.charAt(0) == i) { qpToRemove.add(s); }
      }
    }
    
    for(String s: kwToRemove) { _state.keywords.remove(s); }
    for(String s: qpToRemove) {
      _state.quotes.remove(s);
      _state.quoteEnds.remove(_state.quotePairs.get(s));
      _state.quotePairs.remove(s);
    }
  }

  
  public void whitespace(int... c) {
    ArrayList<String> kwToRemove = new ArrayList<String>();
    ArrayList<String> qpToRemove = new ArrayList<String>();
    for(int i: c) {
      if ((_escape != null) && (i == _escape)) { continue; }
      
      
      _state.whitespace.add(i);

      
      Iterator<String> kit = _state.keywords.iterator();
      while(kit.hasNext()) {
        String s = kit.next();
        if (s.charAt(0) == i) { kwToRemove.add(s); }
      }
      
      
      Iterator<String> qit = _state.quotes.iterator();
      while(qit.hasNext()) {
        String s = qit.next();
        if (s.charAt(0) == i) { qpToRemove.add(s); }
      }
    }
    
    for(String s: kwToRemove) { _state.keywords.remove(s); }
    for(String s: qpToRemove) {
      _state.quotes.remove(s);
      _state.quoteEnds.remove(_state.quotePairs.get(s));
      _state.quotePairs.remove(s);
    }
  }

  
  public void addQuotes(String begin, String end) {
    begin = escape(begin);
    end = escape(end);
    
    
    Iterator<Integer> wit = _state.whitespace.iterator();
    while(wit.hasNext()) {
      int c = wit.next();
      if (begin.charAt(0) == c) {
        throw new QuoteStartsWithWhitespaceException("Cannot add quote pair '"+
                                                     begin+"'-'"+end+"' because the first character of the beginning has "+
                                                     "already been marked as whitespace");
      }
    }
    
    Iterator<String> qit = _state.quotes.iterator();
    while(qit.hasNext()) {
      String s = qit.next();
      if (s.equals(end)) {
        throw new QuoteStartsWithWhitespaceException("Cannot add quote pair '"+begin+"'-'"+end+
                                                     "' because the end is already used as beginning of another quote pair");
      }
    }

    
    String b = null;
    qit = _state.quotes.iterator();
    while(qit.hasNext()) {
      b = qit.next();
      if (b.equals(begin)) { break; }
    }
    if ((b != null) && (qit.hasNext())) {
      _state.quotes.remove(b);
      _state.quoteEnds.remove(_state.quotePairs.get(b));
      _state.quotePairs.remove(b);
    }
    _state.quotes.add(begin);
    _state.quoteEnds.add(end);
    _state.quotePairs.put(begin,end);
    
    
    ArrayList<String> kwToRemove = new ArrayList<String>();
    Iterator<String> kit = _state.keywords.iterator();
    while(kit.hasNext()) {
      String s = kit.next();
      if (s.startsWith(begin)) { kwToRemove.add(s); }
    }
    
    for(String s: kwToRemove) { _state.keywords.remove(s); }
  }
  
  
  public void addKeyword(String kw) {
    kw = escape(kw);

    
    Iterator<Integer> wit = _state.whitespace.iterator();
    while(wit.hasNext()) {
      int c = wit.next();
      if (kw.charAt(0) == c) {
        throw new KeywordStartsWithWhitespaceException("Cannot add keyword '"+
                                                       kw+"' because the first character of the beginning has "+
                                                       "already been marked as whitespace");
      }
    }

    
    Iterator<String> qit = _state.quotes.iterator();
    while(qit.hasNext()) {
      String s = qit.next();
      if (s.startsWith(kw)) {
        throw new KeywordStartsWithQuoteException("Cannot add keyword '"+
                                                  kw+"' because it has the same beginning as the quote pair '"+
                                                  s+"'-'"+_state.quotePairs.get(s)+"'");
      }
    }

    
    _state.keywords.add(kw);
  }
  
  
  public String getNextToken() throws IOException {
    StringBuilder buf = new StringBuilder();
    int c = nextToken();
    while (c!=-1) {
      _isEscape = ((_escape != null) && (((char)c) == _escape));
      
      
      if (_state.whitespace.contains(c)) {
        if (_wasEscape) {
          
          buf.append(String.valueOf((char)c));
          _wasEscape = false;
        }
        else {
          if (buf.length() > 0) {
            _token = Token.NORMAL;
            return buf.toString();
          }
        }
        c = nextToken();
        continue;
      }

      if (!_wasEscape) {
        
        String temp;
        temp = findMatch(c, _state.quotes, new Lambda<String,String>() {
          public String value(String in) {
            
            
            for(int i=in.length()-1; i > 0; --i) {
              pushToken(in.charAt(i));
            }
            return null;
          }
        });
        if (temp != null) {
          
          if (buf.length() > 0) {
            
            
            for(int i=temp.length()-1; i >= 0; --i) {
              pushToken(temp.charAt(i));
            }
            _token = Token.NORMAL;
            return buf.toString();
          }
          String begin = temp;
          Stack<String> quoteStack = new Stack<String>();
          quoteStack.add(begin);
          StringBuilder quoteBuf = new StringBuilder(unescape(begin));
          
          
          
          
          
          pushState();
          _state = new State();
          _state.whitespace.clear();
          _state.keywords.clear();
          _state.keywords.addAll(_stateStack.peek().quotes);
          _state.keywords.addAll(_stateStack.peek().quoteEnds);
          _state.quotes.clear();
          _state.quoteEnds.clear();
          _state.quotePairs.clear();
          
          while(quoteStack.size() > 0) {
            String s = getNextToken();
            if (s == null) { break; }
            if (_stateStack.peek().quoteEnds.contains(s)) {
              
              String top = quoteStack.peek();
              if (_stateStack.peek().quotePairs.get(top).equals(s)) {
                
                quoteBuf.append(unescape(s));
                quoteStack.pop();
              }
              else {
                
                
                if (_stateStack.peek().quotes.contains(s)) {
                  
                  quoteBuf.append(unescape(s));
                  quoteStack.add(s);
                }
                else {
                  
                  quoteBuf.append(s);
                  break;
                }
              }
            }
            else if (_stateStack.peek().quotes.contains(s)) {
              
              quoteBuf.append(unescape(s));
              quoteStack.add(s);
            }
            else {
              quoteBuf.append(s);
            }
          }
          
          
          popState();
          _token = Token.QUOTED;
          return quoteBuf.toString();
        }
      }
      
      if (!_wasEscape) {
        
        String temp = findMatch(c, _state.keywords, new Lambda<String,String>() {
          public String value(String in) {
            
            
            for(int i=in.length()-1; i > 0; --i) {
              pushToken(in.charAt(i));
            }
            return null;
          }
        });
        if (temp != null) {
          
          if (buf.length() > 0) {
            
            
            for(int i=temp.length()-1; i >= 0; --i) {
              pushToken(temp.charAt(i));
            }
            _token = Token.NORMAL;
            return buf.toString();
          }
          _token = Token.KEYWORD;
          return unescape(temp);
        }
      }

      
      
      if (_isEscape) {
        if (_wasEscape) {
          buf.append(String.valueOf(_escape));
          _isEscape = _wasEscape = false;
        }
        else {
          
          
          
          int cnext = nextToken();
          if ((cnext!=(int)_escape) && (!_state.whitespace.contains(cnext))) {
            
            
            String temp = findMatch(cnext, _state.quotes, new Lambda<String,String>() {
              public String value(String in) { 
                
                for(int i=in.length()-1; i > 0; --i) {
                  pushToken(in.charAt(i));
                }
                return null;
              }
            });
            if (temp != null) {
              
              for(int i=temp.length()-1; i > 0; --i) {
                pushToken(temp.charAt(i));
              }
              
            }
            else {
              
              
              temp = findMatch(cnext, _state.keywords, new Lambda<String,String>() {
                public String value(String in) {
                  
                  for(int i=in.length()-1; i > 0; --i) {
                    pushToken(in.charAt(i));
                  }
                  return null;
                }
              });
              if (temp != null) {
                
                for(int i=temp.length()-1; i > 0; --i) {
                  pushToken(temp.charAt(i));
                }
                
              }
              else {
                
                
                
                buf.append(String.valueOf(_escape));
                _isEscape = _wasEscape = false;
              }
            }
          }
          pushToken(cnext);
        }
      }
      else {
        buf.append(String.valueOf((char)c));
      }
      _wasEscape = _isEscape;
      c = nextToken();
    }
    if (_wasEscape) {
      
      
      buf.append(String.valueOf(_escape));
    }
    
    if (buf.length() > 0) {
      _token = Token.NORMAL;
      return buf.toString();
    }
    
    _token = Token.END;
    return null;
  }
  
  
  public static TreeSet<String> prefixSet(Set<String> set, String prefix) {
    TreeSet<String> out = new TreeSet<String>();
    Iterator<String> it = set.iterator();
    while(it.hasNext()) {
      String s = it.next();
      if (s.startsWith(prefix)) { out.add(s); }
    }
    return out;
  }

  protected String findMatch(int c, TreeSet<String> choices, Lambda<String,String> notFoundLambda) throws IOException {
    StringBuilder buf = new StringBuilder(String.valueOf((char)c));
    SortedSet<String> prefixSet = prefixSet(choices,buf.toString());
    while(prefixSet.size()>1) { 
      c = nextToken();
      if (c!=-1) {
        
        buf.append(String.valueOf((char)c));
        prefixSet = prefixSet(choices,buf.toString());
      }
      else {
        
        break;
      }
    }
    if ((c!=-1) && 
        (prefixSet.size() == 1) && 
        (choices.contains(prefixSet.first()))) {
      
      String match = prefixSet.first();
      
      while((c!=-1) && (buf.length()<match.length())) {
        c = nextToken();
        if (c!=-1) {
          
          buf.append(String.valueOf((char)c));
        }
        else {
          
          break;
        }
      }
      if (buf.toString().equals(match)) { return buf.toString(); }
    }
    return notFoundLambda.value(buf.toString());
  }
  
  
  protected String escape(String s) {
    if (_escape == null) { return s; }
    StringBuilder sb = new StringBuilder();
    for(int i = 0; i < s.length(); ++i) {
      if (i == 0) { sb.append(s.charAt(0)); }
      else {
      if (s.charAt(i) == _escape) { sb.append(_escape); }
      sb.append(s.charAt(i));
    }
    }
    return sb.toString();
  }
  
  protected String unescape(String s) {
    if (_escape == null) { return s; }
    StringBuilder sb = new StringBuilder();
    for(int i = 0; i < s.length(); ++i) {
      if (i == 0) { sb.append(s.charAt(0)); }
      else {
      if (s.charAt(i) == _escape) {
        if ((i+1<s.length()) && (s.charAt(i+1) == _escape)) { ++i; }
      }
      sb.append(s.charAt(i));
    }
    }
    return sb.toString();
  }

  
  public static class SetupException extends RuntimeException {
    public SetupException(String s) { super(s); }
  }
  
  
  public static class StartsWithWhitespaceException extends SetupException {
    public StartsWithWhitespaceException(String s) { super(s); }
  }
  
  
  public static class QuoteStartsWithWhitespaceException extends StartsWithWhitespaceException {
    public QuoteStartsWithWhitespaceException(String s) { super(s); }
  }
  
  
  public static class KeywordStartsWithWhitespaceException extends StartsWithWhitespaceException {
    public KeywordStartsWithWhitespaceException(String s) { super(s); }
  }
  
  
  public static class KeywordStartsWithQuoteException extends SetupException {
    public KeywordStartsWithQuoteException(String s) { super(s); }
  }
}
