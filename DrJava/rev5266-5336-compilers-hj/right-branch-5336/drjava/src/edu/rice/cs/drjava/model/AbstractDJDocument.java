

package edu.rice.cs.drjava.model;

import edu.rice.cs.drjava.DrJava;
import edu.rice.cs.drjava.config.OptionConstants;
import edu.rice.cs.drjava.config.OptionEvent;
import edu.rice.cs.drjava.config.OptionListener;

import edu.rice.cs.drjava.model.definitions.indent.Indenter;
import edu.rice.cs.drjava.model.definitions.reducedmodel.BraceInfo;
import edu.rice.cs.drjava.model.definitions.reducedmodel.ReducedModelControl;
import edu.rice.cs.drjava.model.definitions.reducedmodel.HighlightStatus;
import edu.rice.cs.drjava.model.definitions.reducedmodel.ReducedModelState;

import edu.rice.cs.util.OperationCanceledException;
import edu.rice.cs.util.StringOps;
import edu.rice.cs.util.UnexpectedException;
import edu.rice.cs.util.swing.Utilities;
import edu.rice.cs.util.text.SwingDocument;

import java.awt.EventQueue;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.SortedMap;
import java.util.StringTokenizer;
import java.util.TreeMap;
import javax.swing.ProgressMonitor;
import javax.swing.text.AbstractDocument;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.Position;

import static edu.rice.cs.drjava.model.definitions.reducedmodel.ReducedModelStates.*;


public abstract class AbstractDJDocument extends SwingDocument implements DJDocument, OptionConstants {
  
  
  
  protected static final String delimiters = " \t\n\r{}()[].+-/*;:=!@#$%^&*~<>?,\"`'<>|";
  protected static final char newline = '\n';
  
  protected static final HashSet<String> _normEndings = _makeNormEndings();
  
  protected static final HashSet<String> _keywords = _makeKeywords();
  
  protected static final HashSet<String> _primTypes = _makePrimTypes();
  
  protected volatile int _indent = 2;
  
  private static final int INIT_CACHE_SIZE = 0x10000;  
  
  public static final int POS_THRESHOLD = 10000; 


   
  public static final char[] CLOSING_BRACES = new char[] {'}', ')'};
  
  
  


  
  
  public final ReducedModelControl _reduced = new ReducedModelControl();  
  
  
  protected volatile int _currentLocation = 0;
  
  
  private volatile HashMap<Query, Object> _queryCache;
  
  
  private volatile SortedMap<Integer, List<Query>> _offsetToQueries;
  
  
  private volatile Indenter _indenter;
  
  
  private volatile OptionListener<Integer> _listener1;
  private volatile OptionListener<Boolean> _listener2;
  
  
  
  
  protected AbstractDJDocument() { 
    this(new Indenter(DrJava.getConfig().getSetting(INDENT_LEVEL).intValue()));
  }
  
  
  protected AbstractDJDocument(int indentLevel) { 
    this(new Indenter(indentLevel));
  }
  
  
  protected AbstractDJDocument(Indenter indenter) { 
    _indenter = indenter;
    _queryCache = null;
    _offsetToQueries = null;
    _initNewIndenter();


  }
  
  
  
  
  private Indenter getIndenter() { return _indenter; }
  
  
  public int getIndent() { return _indent; }
  
  
  public void setIndent(final int indent) {
    DrJava.getConfig().setSetting(INDENT_LEVEL, indent);
    this._indent = indent;
  }
  
  protected void _removeIndenter() {

    DrJava.getConfig().removeOptionListener(INDENT_LEVEL, _listener1);
    DrJava.getConfig().removeOptionListener(AUTO_CLOSE_COMMENTS, _listener2);
  }
  
  
  private void _initNewIndenter() {
    
    
    final Indenter indenter = _indenter;

    _listener1 = new OptionListener<Integer>() {
      public void optionChanged(OptionEvent<Integer> oce) {

        indenter.buildTree(oce.value);
      }
    };
    
    _listener2 = new OptionListener<Boolean>() {
      public void optionChanged(OptionEvent<Boolean> oce) {

        indenter.buildTree(DrJava.getConfig().getSetting(INDENT_LEVEL));
      }
    };
    
    DrJava.getConfig().addOptionListener(INDENT_LEVEL, _listener1);
    DrJava.getConfig().addOptionListener(AUTO_CLOSE_COMMENTS, _listener2);
  }
  
  
  
  protected static HashSet<String> _makeNormEndings() {
    HashSet<String> normEndings = new HashSet<String>();
    normEndings.add(";");
    normEndings.add("{");
    normEndings.add("}");
    normEndings.add("(");
    return  normEndings;
  }
  
  
  protected static HashSet<String> _makeKeywords() {
    final String[] words =  {
      "import", "native", "package", "goto", "const", "if", "else", "switch", "while", "for", "do", "true", "false",
      "null", "this", "super", "new", "instanceof", "return", "static", "synchronized", "transient", "volatile", 
      "final", "strictfp", "throw", "try", "catch", "finally", "throws", "extends", "implements", "interface", "class",
      "break", "continue", "public", "protected", "private", "abstract", "case", "default", "assert", "enum"
    };
    HashSet<String> keywords = new HashSet<String>();
    for (int i = 0; i < words.length; i++) { keywords.add(words[i]); }
    return  keywords;
  }
  
  
  protected static HashSet<String> _makePrimTypes() {
    final String[] words =  {
      "boolean", "char", "byte", "short", "int", "long", "float", "double", "void",
    };
    HashSet<String> prims = new HashSet<String>();
    for (String w: words) { prims.add(w); }
    return prims;
  }
  


  
  
  public ArrayList<HighlightStatus> getHighlightStatus(int start, int end) {
    
    assert EventQueue.isDispatchThread();
    
    if (start == end) return new ArrayList<HighlightStatus>(0);
    ArrayList<HighlightStatus> v;
    
    setCurrentLocation(start);
    
    v = _reduced.getHighlightStatus(start, end - start);
    
    
    for (int i = 0; i < v.size(); i++) {
      HighlightStatus stat = v.get(i);
      if (stat.getState() == HighlightStatus.NORMAL) i = _highlightKeywords(v, i);
    }
    
    
    
    return v;
  }
  
  
  private int _highlightKeywords(ArrayList<HighlightStatus> v, int i) {
    
    final HighlightStatus original = v.get(i);
    final String text;
    
    
    try { text = getText(original.getLocation(), original.getLength()); }
    catch (BadLocationException e) { throw new UnexpectedException(e); }
    
    
    
    StringTokenizer tokenizer = new StringTokenizer(text, delimiters, true);
    
    
    int start = original.getLocation();
    int length = 0;
    
    
    v.remove(i);
    
    
    int index = i;
    
    boolean process;
    int state = 0;
    while (tokenizer.hasMoreTokens()) {
      String token = tokenizer.nextToken();
      
      
      process = false;
      if (_isType(token)) {
        
        state = HighlightStatus.TYPE;
        process = true;
      } 
      else if (_keywords.contains(token)) {
        state = HighlightStatus.KEYWORD;
        process = true;
      } 
      else if (_isNum(token)) {
        state = HighlightStatus.NUMBER;
        process = true;
      }
      
      if (process) {
        
        if (length != 0) {
          HighlightStatus newStat = new HighlightStatus(start, length, original.getState());
          v.add(index, newStat);
          index++;
          start += length;
          length = 0;
        }
        
        
        int keywordLength = token.length();
        v.add(index, new HighlightStatus(start, keywordLength, state));
        index++;
        
        start += keywordLength;
      }
      else {
        
        length += token.length();
      }
    }
    
    if (length != 0) {
      HighlightStatus newStat = new HighlightStatus(start, length, original.getState());
      v.add(index, newStat);
      index++;
      length = 0;
    }
    
    return index - 1;
  }
  
  
  static boolean _isNum(String x) {
    try {
      Double.parseDouble(x);
      return true;
    } 
    catch (NumberFormatException e) {
      int radix = 10;
      int begin = 0;
      int end = x.length();
      int bits = 32;
      if (end-begin>1) {
        
        char ch = x.charAt(end-1);
        if ((ch=='l')||(ch=='L')) { 
          --end;
          bits = 64;  
        }
        if (end-begin>1) {
          
          if (x.charAt(0) == '0') { 
            ++begin;
            radix = 8;
            if (end-begin>1) {
              
              ch = x.charAt(1);
              if ((ch=='x')||(ch=='X')) { 
                ++begin;
                radix = 16;
              }
            }
          }
        }
      }
      try {
        
        java.math.BigInteger val = new java.math.BigInteger(x.substring(begin, end), radix);
        return (val.bitLength() <= bits);
      }
      catch (NumberFormatException e2) {
        return false;
      }
    }
  }
  
  
  private boolean _isType(String x) {
    if (_primTypes.contains(x)) return true;
    
    try { return Character.isUpperCase(x.charAt(0)); } 
    catch (IndexOutOfBoundsException e) { return false; }
  }
  
  
  public static boolean hasOnlySpaces(String text) { return (text.trim().length() == 0); }
  
  
  protected abstract void _styleChanged(); 
  
  
  private void _addCharToReducedModel(char curChar) {

    _reduced.insertChar(curChar);
  }
  
  
  public int getCurrentLocation() { return _currentLocation; }
  
  
  public void setCurrentLocation(int loc) {
    if (loc < 0) {
      throw new UnexpectedException("Illegal location " + loc);  
    }
    if (loc > getLength()) {
      throw new UnexpectedException("Illegal location " + loc); 
    }
    int dist = loc - _currentLocation;  
    _currentLocation = loc;
    _reduced.move(dist);   

  }
  
  
  public void move(int dist) {
    int newLocation = _currentLocation + dist;
    if (0 <= newLocation && newLocation <= getLength()) {
      _reduced.move(dist);
      _currentLocation = newLocation;
    }
    else throw new IllegalArgumentException("AbstractDJDocument.move(" + dist + ") places the cursor at " + 
                                            newLocation + " which is out of range");
  } 
  
  
  public int balanceBackward() { 
    int origPos = _currentLocation;
    try {
      if (_currentLocation < 2) return -1;
      char prevChar = _getText(_currentLocation - 1, 1).charAt(0);

      if (prevChar != '}' && prevChar != ')' && prevChar != ']') return -1;
      return _reduced.balanceBackward();
    }
    finally { setCurrentLocation(origPos); }
  }
  
  
  public int balanceForward() {
    int origPos = _currentLocation;
    try {
      if (_currentLocation == 0) return -1;
      char prevChar = _getText(_currentLocation - 1, 1).charAt(0);

      if (prevChar != '{' && prevChar != '(' && prevChar != '[') return -1;

      return _reduced.balanceForward() ; 
    }
    finally { setCurrentLocation(origPos); }
  }
  
  
  public ReducedModelControl getReduced() { return _reduced; } 
  
  
  public ReducedModelState stateAtRelLocation(int dist) { return _reduced.moveWalkerGetState(dist); }
  
  
  public ReducedModelState getStateAtCurrent() { 
     assert Utilities.TEST_MODE || EventQueue.isDispatchThread();
    return _reduced.getStateAtCurrent(); 
  }
  
  
  public void resetReducedModelLocation() { _reduced.resetLocation(); }
  
  
  public int findPrevEnclosingBrace(final int pos, final char opening, final char closing) throws BadLocationException {
    
    
    
    final Query key = new Query.PrevEnclosingBrace(pos, opening, closing);
    final Integer cached = (Integer) _checkCache(key);
    if (cached != null) return cached.intValue();
    
    if (pos >= getLength() || pos <= 0) { return -1; }
    
    final char[] delims = {opening, closing};
    int reducedPos = pos;
    int i;  
    int braceBalance = 0;
    
    String text = getText(0, pos);
    
    final int origPos = _currentLocation;
    
    setCurrentLocation(pos);  
    
    
    for (i = pos - 1; i >= 0; i--) {
      
      
      if (match(text.charAt(i), delims)) {
        
        setCurrentLocation(i);  
        reducedPos = i;          
        
        
        
        if (isShadowed()) continue;  
        else {
          
          if (text.charAt(i) == closing) ++braceBalance;
          else {
            if (braceBalance == 0) break; 
            --braceBalance;
          }
        }
      }
    }
    
    
    
    setCurrentLocation(origPos);    
    
    if (i == -1) reducedPos = -1; 
    _storeInCache(key, reducedPos, pos - 1);
    
    
    return reducedPos;  
  }
  
  
  public boolean isShadowed() { return _reduced.isShadowed(); }
  
  
  public boolean isShadowed(int pos) {
    int origPos = _currentLocation;
    setCurrentLocation(pos);
    boolean result = isShadowed();
    setCurrentLocation(origPos);
    return result;
  }
  
  
  public int findNextEnclosingBrace(final int pos, final char opening, final char closing) throws BadLocationException {
    assert EventQueue.isDispatchThread();
    
    
    final Query key = new Query.NextEnclosingBrace(pos, opening, closing);
    final Integer cached = (Integer) _checkCache(key);
    
    if (cached != null) return cached.intValue();
    if (pos >= getLength() - 1) { return -1; }
    
    final char[] delims = {opening, closing};
    int reducedPos = pos;
    int i;  
    int braceBalance = 0;
    
    String text = getText();
    
    final int origPos = _currentLocation;
    
    setCurrentLocation(pos);  
    
    
    for (i = pos + 1; i < text.length(); i++) {
      
      
      if (match(text.charAt(i),delims)) {
        
        setCurrentLocation(i);  
        reducedPos = i;          
        
        
        if (isShadowed()) continue;  
        else {
          
          if (text.charAt(i) == opening) ++braceBalance;
          else {
            if (braceBalance == 0) break; 
            --braceBalance;
          }
        }
      }
    }
    
    
    
    setCurrentLocation(origPos);    
    
    if (i == text.length()) reducedPos = -1; 
    _storeInCache(key, reducedPos, reducedPos);
    
    return reducedPos;  
  }
  
  
  public int findPrevDelimiter(int pos, char[] delims) throws BadLocationException {
    return findPrevDelimiter(pos, delims, true);
  }
  
  
  public int findPrevDelimiter(final int pos, final char[] delims, final boolean skipBracePhrases)
    throws BadLocationException {
    
     assert Utilities.TEST_MODE || EventQueue.isDispatchThread();
    
    
    final Query key = new Query.PrevDelimiter(pos, delims, skipBracePhrases);
    final Integer cached = (Integer) _checkCache(key);
    if (cached != null) {

      return cached.intValue();
    }
    
    int reducedPos = pos;
    int i;  
    int lineStartPos = _getLineStartPos(pos);
    if (lineStartPos < 0) lineStartPos = 0;
    
    if (lineStartPos >= pos) i = lineStartPos - 1;  
    else { 
      assert lineStartPos < pos;
      String line = getText(lineStartPos, pos - lineStartPos);  
      final int origPos = _currentLocation;
      
      
      for (i = pos - 1; i >= lineStartPos; i--) {
        
        
        int irel = i - lineStartPos;
        setCurrentLocation(i);  
        if (isShadowed() || isCommentOpen(line, irel)) {

          continue;
        }
        char ch = line.charAt(irel);
        
        if (match(ch, delims) ) {
          reducedPos = i;    
          break;
        }
        
        if (skipBracePhrases && match(ch, CLOSING_BRACES) ) {  

          setCurrentLocation(i + 1); 

          int dist = balanceBackward();  
          if (dist == -1) { 
            i = -1;

            break;
          }
          assert dist > 0;


          setCurrentLocation(i + 1 - dist);  
          i = _currentLocation;
          
          continue;
        }
      }  
      
      setCurrentLocation(origPos);    
    } 
    
    
    
    if (i < lineStartPos) {  
      if (i <= 0) reducedPos = -1;  
      else reducedPos = findPrevDelimiter(i, delims, skipBracePhrases); 
    }
    
    _storeInCache(key, reducedPos, pos - 1);

    
    
    return reducedPos;  
  }
  
  private static boolean match(char c, char[] delims) {
    for (char d : delims) { if (c == d) return true; } 
    return false;
  }
  
  
  public boolean findCharInStmtBeforePos(char findChar, int position) {
    
     assert Utilities.TEST_MODE || EventQueue.isDispatchThread();
    
    if (position == -1) {
      String msg = 
        "Argument endChar to QuestionExistsCharInStmt must be a char that exists on the current line.";
      throw new UnexpectedException(new IllegalArgumentException(msg));
    }
    
    char[] findCharDelims = {findChar, ';', '{', '}'};
    int prevFindChar;
    
    
    boolean found;
    
    try {
      prevFindChar = this.findPrevDelimiter(position, findCharDelims, false);
      
      if ((prevFindChar == -1) || (prevFindChar < 0)) return false; 
      
      
      String foundString = getText(prevFindChar, 1);
      char foundChar = foundString.charAt(0);
      found = (foundChar == findChar);
    }
    catch (Throwable t) { throw new UnexpectedException(t); }
    return found;
  }
  









  
  
  public int _findPrevCharPos(final int pos, final char[] whitespace) throws BadLocationException {
    
     assert Utilities.TEST_MODE || EventQueue.isDispatchThread();
    
    
    final Query key = new Query.PrevCharPos(pos, whitespace);
    final Integer cached = (Integer) _checkCache(key);
    if (cached != null)  return cached.intValue();
    
    int reducedPos = pos;
    int i = pos - 1;
    String text;
    text = getText(0, pos); 
    
    final int oldPos = _currentLocation;
    
    setCurrentLocation(reducedPos);
    
    
    
    while (i >= 0) { 
      
      
      if (match(text.charAt(i), whitespace)) {
        
        i--;
        continue;
      }
      
      
      setCurrentLocation(i);
      reducedPos = i;                  
      
      
      if ((_reduced.getStateAtCurrent().equals(INSIDE_LINE_COMMENT)) ||
          (_reduced.getStateAtCurrent().equals(INSIDE_BLOCK_COMMENT))) {
        i--;
        continue;
      }
      
      if (i > 0 && _isStartOfComment(text, i - 1)) {   
        
        i = i - 2;
        continue;
      }
      
      
      break;
    }
    
    
    setCurrentLocation(oldPos);
    
    int result = reducedPos;
    if (i < 0) result = -1;
    _storeInCache(key, result, pos - 1);
    return result;
  }
  
  
  protected Object _checkCache(final Query key) {
    if (_queryCache == null) return null;
    return _queryCache.get(key); 
  }
  
  
  protected void _storeInCache(final Query query, final Object answer, final int offset) {
    if (_queryCache == null) return;
    _queryCache.put(query, answer);
    _addToOffsetsToQueries(query, offset);
  }
  
  
  protected void _clearCache(int offset) {
    if (_queryCache == null) return;
    
    if (offset <= 0) {
      _queryCache.clear();
      _offsetToQueries.clear();
      return;
    }
    
    Integer[] deadOffsets = _offsetToQueries.tailMap(offset).keySet().toArray(new Integer[0]);
    for (int i: deadOffsets) {
      for (Query query: _offsetToQueries.get(i)) _queryCache.remove(query);  
      _offsetToQueries.remove(i);   
    }
  }
  
  
  private void _addToOffsetsToQueries(final Query query, final int offset) {
    List<Query> selectedQueries = _offsetToQueries.get(offset);
    if (selectedQueries == null) {
      selectedQueries = new LinkedList<Query>();
      _offsetToQueries.put(offset, selectedQueries);
    }
    selectedQueries.add(query);
  }
  
  
  public void indentLines(int selStart, int selEnd) {
    assert Utilities.TEST_MODE || EventQueue.isDispatchThread();
    try { indentLines(selStart, selEnd, Indenter.IndentReason.OTHER, null); }
    catch (OperationCanceledException oce) {
      
      throw new UnexpectedException(oce);
    }
  }
  
  
  public void indentLines(int selStart, int selEnd, Indenter.IndentReason reason, ProgressMonitor pm)
    throws OperationCanceledException {
    
    assert Utilities.TEST_MODE || EventQueue.isDispatchThread();
    
    
    
    
    try {
      if (selStart == selEnd) {  

        Position oldPosition = createUnwrappedPosition(_currentLocation);
        int lineStart = _getLineStartPos(selStart);
        if (lineStart <  0) lineStart = 0;  
        setCurrentLocation(lineStart);
        

        if (_indentLine(reason)) {
          setCurrentLocation(oldPosition.getOffset()); 
          if (onlyWhiteSpaceBeforeCurrent()) move(_getWhiteSpace());  
        }
      }
      else _indentBlock(selStart, selEnd, reason, pm);
    }
    catch (BadLocationException e) { throw new UnexpectedException(e); }
    
    
    
    endLastCompoundEdit();
  }
  
  
  private void _indentBlock(final int start, final int end, Indenter.IndentReason reason, ProgressMonitor pm)
    throws OperationCanceledException, BadLocationException {
    
    
    _queryCache = new HashMap<Query, Object>(INIT_CACHE_SIZE);
    _offsetToQueries = new TreeMap<Integer, List<Query>>();
    
    
    
    final Position endPos = this.createUnwrappedPosition(end);
    
    int walker = start;

    while (walker < endPos.getOffset()) {
      setCurrentLocation(walker);
      
      Position walkerPos = this.createUnwrappedPosition(walker);
      
      
      _indentLine(reason);  
      
      setCurrentLocation(walkerPos.getOffset());
      walker = walkerPos.getOffset();
      
      if (pm != null) {
        pm.setProgress(walker); 
        if (pm.isCanceled()) throw new OperationCanceledException(); 
      }
      
      
      
      walker += _reduced.getDistToNextNewline() + 1;

    }
    
    
    _queryCache = null;
    _offsetToQueries = null;
  }
  
  
  public boolean _indentLine(Indenter.IndentReason reason) { return getIndenter().indent(this, reason); }
  
  
  public int getIntelligentBeginLinePos(int currPos) throws BadLocationException {
     assert Utilities.TEST_MODE || EventQueue.isDispatchThread();
    
    String prefix;
    int firstChar;
    firstChar = _getLineStartPos(currPos);
    prefix = getText(firstChar, currPos-firstChar);
    
    
    int i;
    int len = prefix.length();
    
    for (i = 0; i < len; i++ ) { if (! Character.isWhitespace(prefix.charAt(i))) break; }
    
    
    if (i < len) {
      int firstRealChar = firstChar + i;
      if (firstRealChar < currPos) return firstRealChar;
    }
    
    return firstChar;
  }
  
  
  public int _getIndentOfCurrStmt(int pos) {
    char[] delims = {';', '{', '}'};
    char[] whitespace = {' ', '\t', '\n', ','};
    return _getIndentOfCurrStmt(pos, delims, whitespace);
  }
  
  
  public int _getIndentOfCurrStmt(int pos, char[] delims) {
    char[] whitespace = {' ', '\t', '\n',','};
    return _getIndentOfCurrStmt(pos, delims, whitespace);
  }
  
  
  public int _getIndentOfCurrStmt(final int pos, final char[] delims, final char[] whitespace)  {
     assert Utilities.TEST_MODE || EventQueue.isDispatchThread();
    
    try {
      
      int lineStart = _getLineStartPos(pos);  
      
      final Query key = new Query.IndentOfCurrStmt(lineStart, delims, whitespace);
      final Integer cached = (Integer) _checkCache(key);
      if (cached != null) return cached;  
      
      
      
      boolean reachedStart = false;
      int prevDelim = findPrevDelimiter(lineStart, delims,  true);
      
      if (prevDelim == -1) reachedStart = true; 
      
      
      int nextNonWSChar;
      if (reachedStart) nextNonWSChar = getFirstNonWSCharPos(0);
      else nextNonWSChar = getFirstNonWSCharPos(prevDelim + 1, whitespace, false);
      
      
      if (nextNonWSChar == -1) nextNonWSChar = getLength();
      
      

      
      
      int newLineStart = _getLineStartPos(nextNonWSChar);
      
      
      int firstNonWS = _getLineFirstCharPos(newLineStart);
      int wSPrefix = firstNonWS - newLineStart;
      _storeInCache(key, wSPrefix, firstNonWS);  
      return wSPrefix;
    }
    catch(BadLocationException e) { throw new UnexpectedException(e); }

  }
  
















  
  
  public int findCharOnLine(final int pos, final char findChar) {
    
     assert Utilities.TEST_MODE || EventQueue.isDispatchThread();  
    
    
    final Query key = new Query.CharOnLine(pos, findChar);
    final Integer cached = (Integer) _checkCache(key);
    if (cached != null) return cached.intValue();
    
    int i;
    int matchIndex; 
    
    try {
      final int oldPos = _currentLocation;
      int lineStart = _getLineStartPos(pos);
      int lineEnd = _getLineEndPos(pos);
      String lineText = getText(lineStart, lineEnd - lineStart);
      i = lineText.indexOf(findChar, 0);
      matchIndex = i + lineStart;
      
      while (i != -1) { 
        
        
        
        setCurrentLocation(matchIndex);  
        
        
        if (_reduced.getStateAtCurrent().equals(FREE)) break; 
        
        
        i = lineText.indexOf(findChar, i+1);
      }
      setCurrentLocation(oldPos);  
      
      if (i == -1) matchIndex = -1;
      _storeInCache(key, matchIndex, Math.max(pos - 1, matchIndex));
    }
    catch (BadLocationException e) { throw new UnexpectedException(e); }
    
    return matchIndex;
  }
  
  
  public int _getLineStartPos(final int pos) {
    
     assert Utilities.TEST_MODE || EventQueue.isDispatchThread();
    
    if (pos < 0 || pos > getLength()) return -1;
    
    final Query key = new Query.LineStartPos(pos);
    final Integer cached = (Integer) _checkCache(key);
    if (cached != null) return cached.intValue();
    
    int dist;
    
    final int oldPos = _currentLocation;
    setCurrentLocation(pos);
    dist = _reduced.getDistToStart(0);
    setCurrentLocation(oldPos);
    
    int newPos = 0;
    if (dist >= 0)  newPos = pos - dist;
    _storeInCache(key, newPos, pos - 1);
    return newPos;  
  }
  
  
  public int _getLineEndPos(final int pos) {
    
     assert Utilities.TEST_MODE || EventQueue.isDispatchThread();
    
    if (pos < 0 || pos > getLength()) return -1;
    
    
    final Query key = new Query.LineEndPos(pos);
    final Integer cached = (Integer) _checkCache(key);
    if (cached != null) return cached.intValue();
    
    int dist, newPos;
    
    final int oldPos = _currentLocation;
    setCurrentLocation(pos);
    dist = _reduced.getDistToNextNewline();
    setCurrentLocation(oldPos);
    
    newPos = pos + dist;
    assert newPos == getLength() || _getText(newPos, 1).charAt(0) == newline;
    _storeInCache(key, newPos, newPos);
    return newPos;
  }
  
  
  public int _getLineFirstCharPos(final int pos) {
    
     assert Utilities.TEST_MODE || EventQueue.isDispatchThread();
    
    
    final Query key = new Query.LineFirstCharPos(pos);
    final Integer cached = (Integer) _checkCache(key);
    if (cached != null)  return cached.intValue();
    
    final int startLinePos = _getLineStartPos(pos);
    final int endLinePos = _getLineEndPos(pos);
    int nonWSPos = endLinePos;
    
    
    String text = _getText(startLinePos, endLinePos - startLinePos);
    int walker = 0;
    while (walker < text.length()) {
      if (text.charAt(walker) == ' ' || text.charAt(walker) == '\t') walker++;
      else {
        nonWSPos = startLinePos + walker;
        break;
      }
    }
    _storeInCache(key, nonWSPos, Math.max(pos - 1, nonWSPos));
    return nonWSPos;  
  }
  
  
  public int getFirstNonWSCharPos(int pos) throws BadLocationException {
    char[] whitespace = {' ', '\t', '\n'};
    return getFirstNonWSCharPos(pos, whitespace, false);
  }
  
  
  public int getFirstNonWSCharPos(int pos, boolean acceptComments) throws BadLocationException {
    char[] whitespace = {' ', '\t', '\n'};
    return getFirstNonWSCharPos(pos, whitespace, acceptComments);
  }
  
  
  public int getFirstNonWSCharPos(final int pos, final char[] whitespace, final boolean acceptComments) throws 
    BadLocationException {
    
     assert Utilities.TEST_MODE || EventQueue.isDispatchThread();
    
    
    final Query key = new Query.FirstNonWSCharPos(pos, whitespace, acceptComments);
    final Integer cached = (Integer) _checkCache(key);
    if (cached != null)  return cached.intValue();
    
    final int docLen = getLength();
    final int origPos = _currentLocation;
    final int endPos = _getLineEndPos(pos);
    
    String line = getText(pos, endPos - pos);   
    setCurrentLocation(pos);  
    try {
      int i = pos;
      int reducedPos = pos;
      
      while (i < endPos) {
        
        
        if (match(line.charAt(i-pos), whitespace)) {
          i++;
          continue;
        }
        
        
        setCurrentLocation(i);  
        reducedPos = i;
        
        
        if (! acceptComments &&
            ((_reduced.getStateAtCurrent().equals(INSIDE_LINE_COMMENT)) ||
             (_reduced.getStateAtCurrent().equals(INSIDE_BLOCK_COMMENT)))) {
          i++;  
          continue;
        }
        
        
        if (! acceptComments && _isStartOfComment(line, i - pos)) {
          
          i = i + 2;  
          continue;
        }
        
        
        _storeInCache(key, reducedPos, reducedPos);  

        return reducedPos;
      }
      
      
      if (endPos + 1 >= docLen) { 
        _storeInCache(key, -1, Integer.MAX_VALUE);  

        return -1;
      }
    }
    finally { setCurrentLocation(origPos); }  
    
    
    return getFirstNonWSCharPos(endPos + 1, whitespace, acceptComments);
  }
  
  public int _findPrevNonWSCharPos(int pos) throws BadLocationException {
    char[] whitespace = {' ', '\t', '\n'};
    return _findPrevCharPos(pos, whitespace);
  }
  
  
  protected static boolean _isStartOfComment(String text, int pos) {
    char currChar = text.charAt(pos);
    if (currChar == '/') {
      try {
        char afterCurrChar = text.charAt(pos + 1);
        if ((afterCurrChar == '/') || (afterCurrChar == '*'))  return true;
      } catch (StringIndexOutOfBoundsException e) { }
    }
    return false;
  }
  
  


  














  
  
  
  public boolean _inParenPhrase(final int pos) {
    
     assert Utilities.TEST_MODE || EventQueue.isDispatchThread();
    
    
    final Query key = new Query.PosInParenPhrase(pos);
    Boolean cached = (Boolean) _checkCache(key);
    if (cached != null) return cached.booleanValue();
    
    boolean _inParenPhrase;
    
    final int oldPos = _currentLocation;
    
    setCurrentLocation(pos);
    _inParenPhrase = _inParenPhrase();
    setCurrentLocation(oldPos);
    _storeInCache(key, _inParenPhrase, pos - 1);
    
    return _inParenPhrase;
  }
  
  
  public BraceInfo _getLineEnclosingBrace() {
    
     assert Utilities.TEST_MODE || EventQueue.isDispatchThread();
    
    
    final int lineStart = _getLineStartPos(_currentLocation);

    if (lineStart < 0) return BraceInfo.NULL;
    final int keyPos = lineStart;
    final Query key = new Query.LineEnclosingBrace(keyPos);
    final BraceInfo cached = (BraceInfo) _checkCache(key);
    if (cached != null) return cached;
    

    BraceInfo b = _reduced._getLineEnclosingBrace();
    
    _storeInCache(key, b, keyPos - 1);
    return b;
  }
  
  
  public BraceInfo _getEnclosingBrace() {
    int pos = _currentLocation;
    
    final Query key = new Query.EnclosingBrace(pos);
    final BraceInfo cached = (BraceInfo) _checkCache(key);
    if (cached != null) return cached;
    BraceInfo b = _reduced._getEnclosingBrace();
    _storeInCache(key, b, pos - 1);
    return b;
  }
  
  
  private boolean _inParenPhrase() {
    
    BraceInfo info = _reduced._getEnclosingBrace(); 
    return info.braceType().equals(BraceInfo.OPEN_PAREN);

  }
  








  























  
  
  public boolean _inBlockComment(final int pos) {
    final int here = _currentLocation;
    final int distToStart = here - _getLineStartPos(here);
    _reduced.resetLocation();
    ReducedModelState state = stateAtRelLocation(-distToStart);
    
    return (state.equals(INSIDE_BLOCK_COMMENT));
  }
  

















  
  
  protected boolean notInBlock(final int pos) {
    
    final Query key = new Query.PosNotInBlock(pos);
    final Boolean cached = (Boolean) _checkCache(key);
    if (cached != null) return cached.booleanValue();
    
    final int oldPos = _currentLocation;
    setCurrentLocation(pos);
    final BraceInfo info = _reduced._getEnclosingBrace();
    final boolean notInParenPhrase = info.braceType().equals(BraceInfo.NONE);
    setCurrentLocation(oldPos);
    _storeInCache(key, notInParenPhrase, pos - 1);
    return notInParenPhrase;
  }
  
  
  private boolean onlyWhiteSpaceBeforeCurrent() throws BadLocationException{
    
    assert Utilities.TEST_MODE || EventQueue.isDispatchThread();
    
    int lineStart = _getLineStartPos(_currentLocation);
    if (lineStart < 0) lineStart = 0;    
    int prefixSize = _currentLocation - lineStart;
    
    
    String prefix = getText(lineStart, prefixSize);
    
    
    int pos = prefixSize - 1;
    while (pos >= 0 && prefix.charAt(pos) == ' ') pos--;
    return (pos < 0);
  }
  
  
  private int _getWhiteSpace() throws BadLocationException {
    
     assert Utilities.TEST_MODE || EventQueue.isDispatchThread();
    
    int lineEnd = _getLineEndPos(_currentLocation);  
    int lineLen = lineEnd - _currentLocation;
    String line = getText(_currentLocation, lineLen);
    int i;
    for (i = 0; i < lineLen && line.charAt(i) == ' '; i++) ;
    return i;
  }
  
  
  private int _getWhiteSpacePrefix() throws BadLocationException {
    

    
     assert Utilities.TEST_MODE || EventQueue.isDispatchThread();
    
    int lineStart = _getLineStartPos(_currentLocation);
    if (lineStart < 0) lineStart = 0;    
    int prefixSize = _currentLocation - lineStart;
    
    
    String prefix = getText(lineStart, prefixSize);
    
    
    int pos = prefixSize - 1;
    while (pos >= 0 && prefix.charAt(pos) == ' ') pos--;
    return (pos < 0) ? prefixSize : 0;
  }
  
  
  public void setTab(int tab, int pos) {
    
     assert Utilities.TEST_MODE || EventQueue.isDispatchThread();
    
    try {
      int startPos = _getLineStartPos(pos);
      int firstNonWSPos = _getLineFirstCharPos(pos);
      int len = firstNonWSPos - startPos;
      
      
      if (len != tab) {
        
        int diff = tab - len;
        if (diff > 0) insertString(firstNonWSPos, StringOps.getBlankString(diff), null);
        else remove(firstNonWSPos + diff, -diff);
      }
       
    }
    catch (BadLocationException e) {
      
      throw new UnexpectedException(e);
    }
  }
  
  
  public void setTab(String tab, int pos) {
    
     assert Utilities.TEST_MODE || EventQueue.isDispatchThread();
    
    try {
      int startPos = _getLineStartPos(pos);
      int firstNonWSPos = _getLineFirstCharPos(pos);
      int len = firstNonWSPos - startPos;
      
      
      remove(startPos, len);
      insertString(startPos, tab, null);
    }
    catch (BadLocationException e) {
      
      throw new UnexpectedException(e);
    }
  }
  
  
  protected void insertUpdate(AbstractDocument.DefaultDocumentEvent chng, AttributeSet attr) {
    
    assert Utilities.TEST_MODE || EventQueue.isDispatchThread();
    
    super.insertUpdate(chng, attr);
    
    try {
      final int offset = chng.getOffset();
      final int length = chng.getLength();
      final String str = getText(offset, length);
      
      if (length > 0) _clearCache(offset);    
      
      Runnable doCommand = 
        (length == 1) ? new CharInsertCommand(offset, str.charAt(0)) : new InsertCommand(offset, str);
      RemoveCommand undoCommand = new UninsertCommand(offset, length, str);
      
      
      addUndoRedo(chng, undoCommand, doCommand);
      
      
      doCommand.run();  
    }
    catch (BadLocationException ble) { throw new UnexpectedException(ble); }
  }
  
  
  protected void removeUpdate(AbstractDocument.DefaultDocumentEvent chng) {
    
    assert Utilities.TEST_MODE || EventQueue.isDispatchThread();
    
    try {
      final int offset = chng.getOffset();
      final int length = chng.getLength();
      
      final String removedText = getText(offset, length);
      super.removeUpdate(chng);
      
      if (length > 0) _clearCache(offset);  
      
      Runnable doCommand = new RemoveCommand(offset, length, removedText);
      Runnable undoCommand = new UnremoveCommand(offset, removedText);
      
      
      addUndoRedo(chng, undoCommand, doCommand);
      
      doCommand.run();
    }
    catch (BadLocationException e) { throw new UnexpectedException(e); }
  }
  
  
  public byte[] getBytes() { return getText().getBytes(); }
  
  public void clear() {
    try { remove(0, getLength()); }
    catch(BadLocationException e) { throw new UnexpectedException(e); }
  }
  
  
  private static boolean isCommentOpen(String text, int pos) {
    int len = text.length();
    if (len < 2) return false;
    if (pos == len - 1) return isCommentStart(text, pos - 1);
    if (pos == 0) return isCommentStart(text, 0);
    return isCommentStart(text, pos - 1) || isCommentStart(text, pos);
  }
  
  
  private static boolean isCommentStart(String text, int pos) {
    char ch1 = text.charAt(pos);
    char ch2 = text.charAt(pos + 1);
    return ch1 == '/' && (ch2 == '/' || ch2 == '*');
  }
  
  
  protected abstract int startCompoundEdit();
  protected abstract void endCompoundEdit(int i);
  protected abstract void endLastCompoundEdit();
  protected abstract void addUndoRedo(AbstractDocument.DefaultDocumentEvent chng, Runnable undoCommand, 
                                      Runnable doCommand);
  
  
  
  
  
  
  
  
  private volatile int _numLinesChangedAfter = -1;
  
  
  
  
  private void _numLinesChanged(int offset) {
    if (_numLinesChangedAfter < 0) {
      _numLinesChangedAfter =  offset;
      return;
    }
    _numLinesChangedAfter = Math.min(_numLinesChangedAfter, offset);
  }
  
  
  public int getAndResetNumLinesChangedAfter() {
    int result = _numLinesChangedAfter;
    _numLinesChangedAfter = -1;
    return result;
  }
  
  protected class InsertCommand implements Runnable {
    protected final int _offset;
    protected final String _text;
    
    public InsertCommand(final int offset, final String text) {
      _offset = offset;
      _text = text;
    }
    
    
    public void run() {
      
      _reduced.move(_offset - _currentLocation);  
      int len = _text.length();
      
      int newLineOffset = _text.indexOf(newline);
      if (newLineOffset >= 0) _numLinesChanged(_offset + newLineOffset);
      
      for (int i = 0; i < len; i++) { _addCharToReducedModel(_text.charAt(i)); }
      
      _currentLocation = _offset + len;  
      _styleChanged();  
      


    }
  }
  
  
  protected class UnremoveCommand extends InsertCommand {
    public UnremoveCommand(final int offset, final String text) { super(offset, text); }
    public void run() {
      super.run();

      
      
      EventQueue.invokeLater(new Runnable() { public void run() { setCurrentLocation(_offset); } });
    }
  }
  
  protected class CharInsertCommand implements Runnable {
    protected final int _offset;
    protected final char _ch;
    
    public CharInsertCommand(final int offset, final char ch) {
      _offset = offset;
      _ch = ch;
    }
    
    
    public void run() {
      
      _reduced.move(_offset - _currentLocation);  
      if (_ch == newline) _numLinesChanged(_offset);  
      _addCharToReducedModel(_ch);
      _currentLocation = _offset + 1;  
      _styleChanged();
    }
  }
  
  protected class RemoveCommand implements Runnable {
    protected final int _offset;
    protected final int _length;
    protected final String _removedText;
    
    public RemoveCommand(final int offset, final int length, final String removedText) {
      _offset = offset;
      _length = length;
      _removedText = removedText;
    }
    
    
    public void run() {
      setCurrentLocation(_offset);
      if (_removedText.indexOf(newline) >= 0) _numLinesChanged(_offset);  
      _reduced.delete(_length);    
      _styleChanged(); 
    }
  }
  
  
  protected class UninsertCommand extends RemoveCommand {
    public UninsertCommand(final int offset, final int length, String text) { super(offset, length, text); }
    public void run() { super.run(); }
  }
}
