

package edu.rice.cs.plt.text;

import java.io.Serializable;
import java.io.StringReader;
import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import edu.rice.cs.plt.iter.IterUtil;
import edu.rice.cs.plt.iter.SizedIterable;
import edu.rice.cs.plt.recur.RecurUtil;
import edu.rice.cs.plt.tuple.Pair;
import edu.rice.cs.plt.collect.OneToOneRelation;
import edu.rice.cs.plt.collect.IndexedOneToOneRelation;
import edu.rice.cs.plt.lambda.Lambda;
import edu.rice.cs.plt.lambda.Thunk;
import edu.rice.cs.plt.lambda.LazyThunk;

public final class TextUtil {
  
  
  public static final String NEWLINE = System.getProperty("line.separator", "\n");
  
  
  public static final String NEWLINE_PATTERN = "\\r\\n|\\n|\\r";
  
  
  private TextUtil() {}
  
  
  public static String toString(Object o) {
    return RecurUtil.safeToString(o);
  }
  
  
  public static SizedIterable<String> getLines(String s) {
    SizedIterable<String> result = IterUtil.<String>empty();
    BufferedReader r = new BufferedReader(new StringReader(s));
    try {
      String line = r.readLine();
      while (line != null) {
        result = IterUtil.compose(result, line);
        line = r.readLine();
      }
    }
    catch (IOException e) {
      
    }
    finally { 
      try { r.close(); }
      catch (IOException e) {  }
    }
    return result;
  }
  
  
  public static String repeat(String s, int copies) {
    StringBuilder result = new StringBuilder();
    for (int i = 0; i < copies; i++) { result.append(s); }
    return result.toString();
  }
  
  
  public static String repeat(char c, int copies) {
    char[] result = new char[copies];
    Arrays.fill(result, c);
    return String.valueOf(result);
  }
  
  
  public static String padLeft(String s, char c, int length) {
    StringBuilder result = new StringBuilder();
    int delta = length - s.length();
    for (int i = 0; i < delta; i++) { result.append(c); }
    result.append(s);
    return result.toString();
  }
  
  
  public static String padRight(String s, char c, int length) {
    StringBuilder result = new StringBuilder();
    result.append(s);
    int delta = length - s.length();
    for (int i = 0; i < delta; i++) { result.append(c); }
    return result.toString();
  }
  
  
  
  
  public static boolean contains(String s, int character) { return s.indexOf(character) >= 0; }
  
  
  public static boolean contains(String s, String piece) { return s.indexOf(piece) >= 0; }
  
  
  public static boolean containsAny(String s, int... characters) {
    for (int c: characters) { if (contains(s, c)) { return true; } }
    return false;
  }
  
  
  public static boolean containsAny(String s, String... pieces) {
    for (String piece: pieces) { if (contains(s, piece)) { return true; } }
    return false;
  }
  
  
  public static boolean containsAll(String s, int... characters) {
    for (int c: characters) { if (!contains(s, c)) { return false; } }
    return true;
  }
  
  
  public static boolean containsAll(String s, String... pieces) {
    for (String piece: pieces) { if (!contains(s, piece)) { return false; } }
    return true;
  }
  
  
  public static boolean containsIgnoreCase(String s, String piece) {
    return s.toLowerCase().indexOf(piece.toLowerCase()) >= 0;
  }
  
  
  public static boolean containsAnyIgnoreCase(String s, String... pieces) {
    for (String piece: pieces) { if (contains(s, piece)) { return true; } }
    return false;
  }
  
  
  public static boolean containsAllIgnoreCase(String s, String... pieces) {
    for (String piece: pieces) { if (!contains(s, piece)) { return false; } }
    return true;
  }
  
  
  public static boolean startsWithAny(String s, String... prefixes) {
    for (String prefix : prefixes) { if (s.startsWith(prefix)) { return true; } }
    return false;
  }
  
  
  public static boolean endsWithAny(String s, String... suffixes) {
    for (String suffix : suffixes) { if (s.endsWith(suffix)) { return true; } }
    return false;
  }
  
  
  public static int indexOfFirst(String s, int... characters) {
    int result = -1;
    for (int c : characters) {
      int index = s.indexOf(c);
      if (index >= 0 && (result < 0 || index < result)) { result = index; }
    }
    return result;
  }
  
  
  public static int indexOfFirst(String s, String... pieces) {
    int result = -1;
    for (String piece : pieces) {
      int index = s.indexOf(piece);
      if (index >= 0 && (result < 0 || index < result)) { result = index; }
    }
    return result;
  }
  
  
  public static String prefix(String s, int delim) {
    int index = s.indexOf(delim);
    return (index == -1) ? s : s.substring(0, index);
  }
  
  
  public static String removePrefix(String s, int delim) {
    int index = s.indexOf(delim);
    return (index == -1) ? s : s.substring(index+1);
  }
    
  
  public static String suffix(String s, int delim) {
    int index = s.lastIndexOf(delim);
    return (index == -1) ? s : s.substring(index+1);
  }
  
  
  public static String removeSuffix(String s, int delim) {
    int index = s.lastIndexOf(delim);
    return (index == -1) ? s : s.substring(0, index);
  }
  
  
  public static SplitString splitWithParens(String s, String delimRegex) {
    return new StringSplitter(s, delimRegex, 0, Bracket.PARENTHESES).split();
  }
  
  
  public static SplitString splitWithParens(String s, String delimRegex, int limit) {
    return new StringSplitter(s, delimRegex, limit, Bracket.PARENTHESES).split();
  }
  
  
  public static SplitString split(String s, String delimRegex, Bracket... brackets) {
    return new StringSplitter(s, delimRegex, 0, brackets).split();
  }
  
  
  public static SplitString split(String s, String delimRegex, int limit, Bracket... brackets) {
    return new StringSplitter(s, delimRegex, limit, brackets).split();
  }
  
  
  public static class SplitString implements Serializable {
    private final List<String> _splits;
    private final List<String> _delims;
    private final String _rest;
    
    private SplitString(List<String> splits, List<String> delims, String rest) {
      _splits = Collections.unmodifiableList(splits);
      _delims = Collections.unmodifiableList(delims);
      _rest = rest;
    }
    
    
    public List<String> splits() { return _splits; }
    
    public List<String> delimiters() { return _delims; }
    
    public String rest() { return _rest; }
    
    
    public String[] array() {
      String[] result = new String[_splits.size() + 1];
      _splits.toArray(result);
      result[_splits.size()] = _rest;
      return result;
    }
    
    public String toString() {
      StringBuilder result = new StringBuilder();
      result.append("SplitString: ");
      for (Pair<String, String> pair : IterUtil.zip(_splits, _delims)) {
        result.append("(").append(pair.first()).append(") ");
        result.append("[").append(pair.second()).append("] ");
      }
      result.append("+ (").append(_rest).append(")");
      return result.toString();
    }
  }
  
  
  private static class StringSplitter {
    private final List<String> _splits;
    private final List<String> _delims;
    private final String _s;
    
    private final Matcher _delim;
    private final Bracket[] _brackets;
    private final Matcher[] _lefts;
    private final Matcher[] _rights;
    private final LinkedList<Integer> _stack; 
    
    private int _remaining;
    
    public StringSplitter(String s, String delimRegex, int limit, Bracket... brackets) {
      if (limit > 0 && limit < 10) { 
        _splits = new ArrayList<String>(limit);
        _delims = new ArrayList<String>(limit);
      }
      else {
        _splits = new ArrayList<String>();
        _delims = new ArrayList<String>();
      }
      _s = s;
      _delim = Pattern.compile(delimRegex).matcher(_s);
      _brackets = brackets;
      _lefts = new Matcher[_brackets.length];
      _rights = new Matcher[_brackets.length];
      for (int i = 0; i < _brackets.length; i++) {
        _lefts[i] = _brackets[i].left().matcher(_s);
        _rights[i] = _brackets[i].right().matcher(_s);
      }
      _stack = new LinkedList<Integer>();
      _remaining = limit;
    }
    
    public SplitString split() {
      int rest = 0; 
      int cursor = 0; 
      while (_remaining != 1) {
        if (_delim.find()) {
          int dStart = _delim.start();
          int dEnd = _delim.end();
          processStack(cursor, dStart, false);
          if (_stack.isEmpty()) {
            _splits.add(_s.substring(rest, dStart));
            _delims.add(_s.substring(dStart, dEnd));
            if (_remaining > 1) { _remaining--; }
            rest = dEnd;
            cursor = dEnd;
          }
          else {
            cursor = processStack(dStart, _s.length(), true);
            _delim.region(cursor, _s.length()); 
          }
        }
        else { _remaining = 1;  }
      }
      return new SplitString(_splits, _delims, _s.substring(rest));
    }
    
    
    private int processStack(int rangeStart, int rangeEnd, boolean stopWhenEmpty) {
      
      
      Boolean[] leftMatches = new Boolean[_lefts.length];
      Boolean[] rightMatches = new Boolean[_rights.length];
      int cursor = rangeStart;
      boolean searchLefts = _stack.isEmpty() || _brackets[_stack.getFirst()].nests();
      
      while (cursor < rangeEnd && !(stopWhenEmpty && _stack.isEmpty())) {
        
        int first = rangeEnd;
        int firstIndex = -1;
        boolean firstIsLeft = false;
        if (!_stack.isEmpty()) { 
          int i = _stack.getFirst();
          Matcher m = _rights[i];
          Boolean matched = rightMatches[i];
          if (matched == null || (matched && m.start() < cursor) || (!matched && m.regionEnd() < first)) {
            matched = m.region(cursor, first).find();
            rightMatches[i] = matched;
          }
          if (matched && m.start() < first) {
            first = m.start();
            firstIndex = i;
            firstIsLeft = false;
          }
        }
        if (searchLefts) { 
          for (int i = 0; i < _lefts.length; i++) {
            Matcher m = _lefts[i];
            Boolean matched = leftMatches[i];
            if (matched == null || (matched && m.start() < cursor) || (!matched && m.regionEnd() < first)) {
              
              
              matched = m.region(cursor, first).find();
              leftMatches[i] = matched;
            }
            if (matched && m.start() < first) {
              first = m.start();
              firstIndex = i;
              firstIsLeft = true;
            }
          }
        }
        
        if (first < rangeEnd) { 
          if (firstIsLeft) {
            _stack.addFirst(firstIndex);
            cursor = _lefts[firstIndex].end();
            searchLefts = _brackets[firstIndex].nests();
          }
          else {
            _stack.removeFirst();
            cursor = _rights[firstIndex].end();
            searchLefts = true; 
          }
        }
        else { cursor = rangeEnd; }
        
      }
      return cursor;
    }
    
  }

  
  public static String toHexString(byte[] bs) {
    return toHexString(bs, 0, bs.length);
  }
  
  
  public static String toHexString(byte[] bs, int offset, int length) {
    StringBuilder result = new StringBuilder();
    for (int i = offset; i < offset+length; i++) {
      if (i > offset) { result.append(' '); }
      byte b = bs[i];
      
      
      result.append(Character.forDigit((b & 0xf0) >> 4, 16));
      result.append(Character.forDigit(b & 0xf, 16));
    }
    return result.toString();
  }
  
  
  public static boolean isDecimalDigit(char c) { return c >= '0' && c <= '9'; }
  
  public static boolean isOctalDigit(char c) { return c >= '0' && c <= '7'; }
  
  public static boolean isHexDigit(char c) {
    return (c >= '0' && c <= '9') || (c >= 'a' && c <= 'f') || (c >= 'A' && c <= 'F');
  }
  
  
  private static abstract class StringTranslator implements Lambda<String, String> {
    protected final StringBuilder _result;
    protected boolean _changed;
    
    StringTranslator() { _result = new StringBuilder(); _changed = false; }
    
    public final String value(String s) {
      int length = s.length();
      for (int i = 0; i < length; i++) { processChar(s.charAt(i)); }
      finish();
      return _changed ? _result.toString() : s;
    }
    
    protected abstract void processChar(char c);
    
    protected abstract void finish();
  }
  
  
  private static abstract class UnicodeTranslator extends StringTranslator {
    private static enum State { START, BACKSLASH, U, DIG1, DIG2, DIG3 };

    private State _state;
    private StringBuilder _buffer; 
    
    UnicodeTranslator() { _state = State.START; _buffer = new StringBuilder(); }
    
    protected abstract void handleStandardChar(char c, boolean backslashed);
    protected abstract void handlePartialEscape(String escape);
    protected abstract void handleCompleteEscape(String escape);
    
    private void reset(char c) {
      handlePartialEscape(_buffer.toString());
      _buffer.delete(0, _buffer.length());
      _state = State.START;
      if (c == '\\') { _state = State.BACKSLASH; }
      else { handleStandardChar(c, false); }
    }
    
    protected final void processChar(char c) {
      switch (_state) {
        case START:
          if (c == '\\') { _state = State.BACKSLASH; }
          else { handleStandardChar(c, false); }
          break;
        case BACKSLASH:
          if (c == 'u') { _state = State.U; }
          else { handleStandardChar(c, true); _state = State.START; } 
          break;
        case U:
          if (isHexDigit(c)) { _buffer.append(c); _state = State.DIG1; }
          else if (c != 'u') { reset(c); }
          break;
        case DIG1:
          if (isHexDigit(c)) { _buffer.append(c); _state = State.DIG2; }
          else { reset(c); }
          break;
        case DIG2:
          if (isHexDigit(c)) { _buffer.append(c); _state = State.DIG3; }
          else { reset(c); }
          break;
        case DIG3:
          if (isHexDigit(c)) {
            _buffer.append(c);
            handleCompleteEscape(_buffer.toString());
            _buffer.delete(0, _buffer.length());
            _state = State.START;
          }
          else { reset(c); }
          break;
      }
    }
    
    protected final void finish() {
      switch (_state) {
        case START: break;
        case BACKSLASH: handleStandardChar('\\', false); break;
        default: handlePartialEscape(_buffer.toString()); break;
      }
    }
  }

  
  public static String unicodeEscape(String s) {
    return new UnicodeTranslator() {
      protected void handleStandardChar(char c, boolean backslashed) {
        if (c > '\u') {
          if (backslashed) { _result.append("\\u"); } 
          _result.append("\\u");
          _result.append(padLeft(Integer.toHexString(c), '0', 4));
          _changed = true;
        }
        else {
          if (backslashed) { _result.append('\\'); }
          _result.append(c);
        }
      }
      protected void handlePartialEscape(String escape) {
        _result.append("\\u"); 
        _result.append(escape);
        _changed = true;
      }
      protected void handleCompleteEscape(String escape) {
        _result.append("\\uu"); 
        _result.append(escape);
        _changed = true;
      }
    }.value(s);
  }
  
  
  public static String unicodeUnescapeOnce(String s) {
    return new UnicodeTranslator() {
      protected void handleStandardChar(char c, boolean backslashed) {
        if (backslashed) { _result.append('\\'); }
        _result.append(c);
      }
      protected void handlePartialEscape(String escape) {
        throw new IllegalArgumentException("Expected a hexadecimal digit after '\\u" + escape + "'");
      }
      protected void handleCompleteEscape(String escape) {
        if (escape.charAt(0) == 'u') {
          _result.append('\\');
          _result.append(escape); 
        }
        else { _result.append((char) Integer.parseInt(escape, 16)); }
        _changed = true;
      }
    }.value(s);
  }

  
  public static String unicodeUnescape(String s) {
    return new UnicodeTranslator() {
      protected void handleStandardChar(char c, boolean backslashed) {
        if (backslashed) { _result.append('\\'); }
        _result.append(c);
      }
      protected void handlePartialEscape(String escape) {
        throw new IllegalArgumentException("Expected a hexadecimal digit after '\\u" + escape + "'");
      }
      protected void handleCompleteEscape(String escape) {
        int firstDigit = escape.lastIndexOf('u') + 1;
        _result.append((char) Integer.parseInt(escape.substring(firstDigit), 16));
        _changed = true;
      }
    }.value(s);
  }
  
  
  public static String javaEscape(String s) {
    return new StringTranslator() {
      protected void processChar(char c) {
        switch (c) {
          case '\b': _result.append("\\b"); _changed = true; break;
          case '\t': _result.append("\\t"); _changed = true; break;
          case '\n': _result.append("\\n"); _changed = true; break;
          case '\f': _result.append("\\f"); _changed = true; break;
          case '\r': _result.append("\\r"); _changed = true; break;
          case '\"': _result.append("\\\""); _changed = true; break;
          case '\'': _result.append("\\\'"); _changed = true; break;
          case '\\': _result.append("\\\\"); _changed = true; break;
          default:
            if (c < ' ' || c == '\u') {
              
              _result.append('\\');
              _result.append(padLeft(Integer.toOctalString(c), '0', 3));
              _changed = true;
            }
            else { _result.append(c); }
            break;
        }
      }
      protected void finish() {}
    }.value(s);
  }
  
  private static enum JState { START, BACKSLASH, DIG1, DIG2, DIG3 };

  
  public static String javaUnescape(String s) {
    return new StringTranslator() {
      
      private JState _state = JState.START;
      private final StringBuilder _buffer = new StringBuilder(); 
      
      private void reset(char c) {
        _result.append((char) Integer.parseInt(_buffer.toString(), 8));
        _buffer.delete(0, _buffer.length());
        _state = JState.START;
        if (c == '\\') { _state = JState.BACKSLASH; _changed = true; }
        else { _result.append(c); }
      }
      
      protected void processChar(char c) {
        switch (_state) {
          case START:
            if (c == '\\') { _state = JState.BACKSLASH; _changed = true; }
            else { _result.append(c); }
            break;
          case BACKSLASH:
            switch (c) {
              case 'b': _result.append('\b'); _state = JState.START; break;
              case 't': _result.append('\t'); _state = JState.START; break;
              case 'n': _result.append('\n'); _state = JState.START; break;
              case 'f': _result.append('\f'); _state = JState.START; break;
              case 'r': _result.append('\r'); _state = JState.START; break;
              case '\"': _result.append('\"'); _state = JState.START; break;
              case '\'': _result.append('\''); _state = JState.START; break;
              case '\\': _result.append('\\'); _state = JState.START; break;
              case '0':
              case '1':
              case '2':
              case '3':
                _buffer.append(c); _state = JState.DIG1; break;
              case '4':
              case '5':
              case '6':
              case '7':
                _buffer.append(c); _state = JState.DIG2; break;
              default:
                throw new IllegalArgumentException("'" + c + "' after '\\'");
            }
            break;
          case DIG1:
            if (isOctalDigit(c)) { _buffer.append(c); _state = JState.DIG2; }
            else { reset(c); }
            break;
          case DIG2:
            if (isOctalDigit(c)) { _buffer.append(c); _state = JState.DIG3; }
            else { reset(c); }
            break;
          case DIG3:
            reset(c);
            break;
        }
      }
      
      protected void finish() {
        switch (_state) {
          case START: break;
          case BACKSLASH: throw new IllegalArgumentException("Nothing after after '\\'");
          default: _result.append((char) Integer.parseInt(_buffer.toString(), 8)); break;
        }
      }
    }.value(s);
  }

  
  public static String regexEscape(String s) {
    return new StringTranslator() {
      protected void processChar(char c) {
        switch (c) {
          case '\t': _result.append("\\t"); _changed = true; break;
          case '\n': _result.append("\\n"); _changed = true; break;
          case '\r': _result.append("\\r"); _changed = true; break;
          case '\f': _result.append("\\f"); _changed = true; break;
          case '\u': _result.append("\\a"); _changed = true; break;
          case '\u': _result.append("\\e"); _changed = true; break;
          default:
            if (c < ' ' || c == '\u') {
              _result.append("\\x");
              _result.append(padLeft(Integer.toHexString(c), '0', 2));
              _changed = true;
            }
            else if ((c > ' ' && c < '0') || (c > '9' && c < 'A') ||
                     (c > 'Z' && c < 'a') || (c > 'z' && c < '\u')) {
              _result.append('\\');
              _result.append(c);
              _changed = true;
            }
            else { _result.append(c); }
            break;
        }
      }
      protected void finish() {}
    }.value(s);
  }

  
  public static String sgmlEscape(String s, final Map<Character, String> entities, final boolean convertToAscii) {
    return new StringTranslator() {
      protected void processChar(char c) {
        String entity = entities.get(c);
        if (entity != null) {
          _result.append('&');
          _result.append(entity);
          _result.append(';');
          _changed = true;
        }
        else if (convertToAscii && c > '\u') {
          _result.append("&#");
          _result.append((int) c);
          _result.append(';');
          _changed = true;
        }
        else { _result.append(c); }
      }
      protected void finish() {}
    }.value(s);
  }

  private static enum SGMLState { START, AMP, NAME, NUM, HEX_DIGITS, DEC_DIGITS };
  
  
  public static String sgmlUnescape(String s, final Map<String, Character> entities) {
    return new StringTranslator() {

      private SGMLState _state = SGMLState.START;
      private final StringBuilder _buffer = new StringBuilder(); 
      
      private void reset() { _buffer.delete(0, _buffer.length()); _state = SGMLState.START; }
      
      protected void processChar(char c) {
        switch (_state) {
          case START:
            if (c == '&') { _state = SGMLState.AMP; _changed = true; }
            else { _result.append(c); }
            break;
          case AMP:
            if (c == '#') { _state = SGMLState.NUM; }
            else if (c == ';') { throw new IllegalArgumentException("Missing entity name"); }
            else { _state = SGMLState.NAME; _buffer.append(c); }
            break;
          case NAME:
            if (c == ';') {
              Character namedChar = entities.get(_buffer.toString());
              if (namedChar == null) {
                throw new IllegalArgumentException("Unrecognized entity name: '" + _buffer.toString() + "'");
              }
              else { _result.append((char) namedChar); reset(); }
            }
            else { _buffer.append(c); }
            break;
          case NUM:
            if (c == 'x') { _state = SGMLState.HEX_DIGITS; }
            else if (isDecimalDigit(c)) { _state = SGMLState.DEC_DIGITS; _buffer.append(c); }
            else { throw new IllegalArgumentException("Expected decimal digit: '" + c + "'"); }
            break;
          case HEX_DIGITS:
            if (c == ';') {
              if (_buffer.length() == 0) { throw new IllegalArgumentException("Expected hexadecimal digit: ';'"); }
              else { _result.append((char) Integer.parseInt(_buffer.toString(), 16)); reset(); }
            }
            else if (isHexDigit(c)) { _buffer.append(c); }
            else { throw new IllegalArgumentException("Expected hexadecimal digit: '" + c + "'"); }
            break;
          case DEC_DIGITS:
            if (c == ';') { _result.append((char) Integer.parseInt(_buffer.toString())); reset(); }
            else if (isDecimalDigit(c)) { _buffer.append(c); }
            else { throw new IllegalArgumentException("Expected decimal digit: '" + c + "'"); }
            break;
        }
      }
      
      protected void finish() {
        if (_state != SGMLState.START) { throw new IllegalArgumentException("Unfinished entity"); }
      }
    }.value(s);
  }

  
  public static String xmlEscape(String s) {
    return sgmlEscape(s, XML_ENTITIES.value().functionMap(), true);
  }

 
  public static String xmlEscape(String s, boolean convertToAscii) {
    return sgmlEscape(s, XML_ENTITIES.value().functionMap(), convertToAscii);
  }

 
  public static String xmlUnescape(String s) {
    return sgmlUnescape(s, XML_ENTITIES.value().injectionMap());
  }

  
  public static String htmlEscape(String s) {
    return sgmlEscape(s, HTML_ENTITIES.value().functionMap(), true);
  }

 
  public static String htmlUnescape(String s) {
    return sgmlUnescape(s, HTML_ENTITIES.value().injectionMap());
  }

  
  
  private static final Thunk<OneToOneRelation<Character, String>> XML_ENTITIES = 
    LazyThunk.make(new Thunk<OneToOneRelation<Character, String>>() {
    public OneToOneRelation<Character, String> value() {
      OneToOneRelation<Character, String> result = new IndexedOneToOneRelation<Character, String>();
      
      result.add('"', "quot");
      result.add('&', "amp");
      result.add('\'', "apos");
      result.add('<', "lt");
      result.add('>', "gt");
      return result;
    }      
  });

  
  
  private static final Thunk<OneToOneRelation<Character, String>> HTML_ENTITIES = 
    LazyThunk.make(new Thunk<OneToOneRelation<Character, String>>() {
    public OneToOneRelation<Character, String> value() {
      OneToOneRelation<Character, String> result = new IndexedOneToOneRelation<Character, String>();
      
      result.add('\'', "#39"); 
      result.add('"', "quot");
      result.add('&', "amp");
      result.add('<', "lt");
      result.add('>', "gt");
      
      result.add('\u', "nbsp");
      result.add('\u', "iexcl");
      result.add('\u', "cent");
      result.add('\u', "pound");
      result.add('\u', "curren");
      result.add('\u', "yen");
      result.add('\u', "brvbar");
      result.add('\u', "sect");
      result.add('\u', "uml");
      result.add('\u', "copy");
      result.add('\u', "ordf");
      result.add('\u', "laquo");
      result.add('\u', "not");
      result.add('\u', "shy");
      result.add('\u', "reg");
      result.add('\u', "macr");
      result.add('\u', "deg");
      result.add('\u', "plusmn");
      result.add('\u', "sup2");
      result.add('\u', "sup3");
      result.add('\u', "acute");
      result.add('\u', "micro");
      result.add('\u', "para");
      result.add('\u', "middot");
      result.add('\u', "cedil");
      result.add('\u', "sup1");
      result.add('\u', "ordm");
      result.add('\u', "raquo");
      result.add('\u', "frac14");
      result.add('\u', "frac12");
      result.add('\u', "frac34");
      result.add('\u', "iquest");
      result.add('\u', "Agrave");
      result.add('\u', "Aacute");
      result.add('\u', "Acirc");
      result.add('\u', "Atilde");
      result.add('\u', "Auml");
      result.add('\u', "Aring");
      result.add('\u', "AElig");
      result.add('\u', "Ccedil");
      result.add('\u', "Egrave");
      result.add('\u', "Eacute");
      result.add('\u', "Ecirc");
      result.add('\u', "Euml");
      result.add('\u', "Igrave");
      result.add('\u', "Iacute");
      result.add('\u', "Icirc");
      result.add('\u', "Iuml");
      result.add('\u', "ETH");
      result.add('\u', "Ntilde");
      result.add('\u', "Ograve");
      result.add('\u', "Oacute");
      result.add('\u', "Ocirc");
      result.add('\u', "Otilde");
      result.add('\u', "Ouml");
      result.add('\u', "times");
      result.add('\u', "Oslash");
      result.add('\u', "Ugrave");
      result.add('\u', "Uacute");
      result.add('\u', "Ucirc");
      result.add('\u', "Uuml");
      result.add('\u', "Yacute");
      result.add('\u', "THORN");
      result.add('\u', "szlig");
      result.add('\u', "agrave");
      result.add('\u', "aacute");
      result.add('\u', "acirc");
      result.add('\u', "atilde");
      result.add('\u', "auml");
      result.add('\u', "aring");
      result.add('\u', "aelig");
      result.add('\u', "ccedil");
      result.add('\u', "egrave");
      result.add('\u', "eacute");
      result.add('\u', "ecirc");
      result.add('\u', "euml");
      result.add('\u', "igrave");
      result.add('\u', "iacute");
      result.add('\u', "icirc");
      result.add('\u', "iuml");
      result.add('\u', "eth");
      result.add('\u', "ntilde");
      result.add('\u', "ograve");
      result.add('\u', "oacute");
      result.add('\u', "ocirc");
      result.add('\u', "otilde");
      result.add('\u', "ouml");
      result.add('\u', "divide");
      result.add('\u', "oslash");
      result.add('\u', "ugrave");
      result.add('\u', "uacute");
      result.add('\u', "ucirc");
      result.add('\u', "uuml");
      result.add('\u', "yacute");
      result.add('\u', "thorn");
      result.add('\u', "yuml");
      
      result.add('\u', "OElig");
      result.add('\u', "oelig");
      result.add('\u', "Scaron");
      result.add('\u', "scaron");
      result.add('\u', "Yuml");
      result.add('\u', "fnof");
      
      result.add('\u', "circ");
      result.add('\u', "tilde");
      
      result.add('\u', "Alpha");
      result.add('\u', "Beta");
      result.add('\u', "Gamma");
      result.add('\u', "Delta");
      result.add('\u', "Epsilon");
      result.add('\u', "Zeta");
      result.add('\u', "Eta");
      result.add('\u', "Theta");
      result.add('\u', "Iota");
      result.add('\u', "Kappa");
      result.add('\u', "Lambda");
      result.add('\u', "Mu");
      result.add('\u', "Nu");
      result.add('\u', "Xi");
      result.add('\u', "Omicron");
      result.add('\u', "Pi");
      result.add('\u', "Rho");
      result.add('\u', "Sigma");
      result.add('\u', "Tau");
      result.add('\u', "Upsilon");
      result.add('\u', "Phi");
      result.add('\u', "Chi");
      result.add('\u', "Psi");
      result.add('\u', "Omega");
      
      result.add('\u', "alpha");
      result.add('\u', "beta");
      result.add('\u', "gamma");
      result.add('\u', "delta");
      result.add('\u', "epsilon");
      result.add('\u', "zeta");
      result.add('\u', "eta");
      result.add('\u', "theta");
      result.add('\u', "iota");
      result.add('\u', "kappa");
      result.add('\u', "lambda");
      result.add('\u', "mu");
      result.add('\u', "nu");
      result.add('\u', "xi");
      result.add('\u', "omicron");
      result.add('\u', "pi");
      result.add('\u', "rho");
      result.add('\u', "sigmaf");
      result.add('\u', "sigma");
      result.add('\u', "tau");
      result.add('\u', "upsilon");
      result.add('\u', "phi");
      result.add('\u', "chi");
      result.add('\u', "psi");
      result.add('\u', "omega");
      
      result.add('\u', "thetasym");
      result.add('\u', "upsih");
      result.add('\u', "piv");
      
      result.add('\u', "ensp");
      result.add('\u', "emsp");
      result.add('\u', "thinsp");
      result.add('\u', "zwnj");
      result.add('\u', "zwj");
      result.add('\u', "lrm");
      result.add('\u', "rlm");
      result.add('\u', "ndash");
      result.add('\u', "mdash");
      result.add('\u', "lsquo");
      result.add('\u', "rsquo");
      result.add('\u', "sbquo");
      result.add('\u', "ldquo");
      result.add('\u', "rdquo");
      result.add('\u', "bdquo");
      result.add('\u', "dagger");
      result.add('\u', "Dagger");
      result.add('\u', "bull");
      result.add('\u', "hellip");
      result.add('\u', "permil");
      result.add('\u', "prime");
      result.add('\u', "Prime");
      result.add('\u', "lsaquo");
      result.add('\u', "rsaquo");
      result.add('\u', "oline");
      result.add('\u', "frasl");
      result.add('\u', "euro");
      
      result.add('\u', "image");
      result.add('\u', "weierp");
      result.add('\u', "real");
      result.add('\u', "trade");
      result.add('\u', "alefsym");
      result.add('\u', "larr");
      result.add('\u', "uarr");
      result.add('\u', "rarr");
      result.add('\u', "darr");
      result.add('\u', "harr");
      result.add('\u', "crarr");
      result.add('\u', "lArr");
      result.add('\u', "uArr");
      result.add('\u', "rArr");
      result.add('\u', "dArr");
      result.add('\u', "hArr");
      
      result.add('\u', "forall");
      result.add('\u', "part");
      result.add('\u', "exist");
      result.add('\u', "empty");
      result.add('\u', "nabla");
      result.add('\u', "isin");
      result.add('\u', "notin");
      result.add('\u', "ni");
      result.add('\u', "prod");
      result.add('\u', "sum");
      result.add('\u', "minus");
      result.add('\u', "lowast");
      result.add('\u', "radic");
      result.add('\u', "prop");
      result.add('\u', "infin");
      result.add('\u', "ang");
      result.add('\u', "and");
      result.add('\u', "or");
      result.add('\u', "cap");
      result.add('\u', "cup");
      result.add('\u', "int");
      result.add('\u', "there4");
      result.add('\u', "sim");
      result.add('\u', "cong");
      result.add('\u', "asymp");
      result.add('\u', "ne");
      result.add('\u', "equiv");
      result.add('\u', "le");
      result.add('\u', "ge");
      result.add('\u', "sub");
      result.add('\u', "sup");
      result.add('\u', "nsub");
      result.add('\u', "sube");
      result.add('\u', "supe");
      result.add('\u', "oplus");
      result.add('\u', "otimes");
      result.add('\u', "perp");
      result.add('\u', "sdot");
      
      result.add('\u', "lceil");
      result.add('\u', "rceil");
      result.add('\u', "lfloor");
      result.add('\u', "rfloor");
      result.add('\u', "lang");
      result.add('\u', "rang");
      
      result.add('\u', "loz");
      
      result.add('\u', "spades");
      result.add('\u', "clubs");
      result.add('\u', "hearts");
      result.add('\u', "diams");
      return result;
    }
  });
  
}
