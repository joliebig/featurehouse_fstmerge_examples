

package edu.rice.cs.drjava.model;

import edu.rice.cs.drjava.DrJava;
import edu.rice.cs.drjava.config.OptionConstants;
import edu.rice.cs.drjava.config.OptionEvent;
import edu.rice.cs.drjava.config.OptionListener;
import edu.rice.cs.util.text.SwingDocument;
import edu.rice.cs.util.OperationCanceledException;
import edu.rice.cs.util.UnexpectedException;
import edu.rice.cs.drjava.model.definitions.DefinitionsDocument;
import edu.rice.cs.drjava.model.definitions.indent.Indenter;
import edu.rice.cs.drjava.model.definitions.reducedmodel.BraceReduction;
import edu.rice.cs.drjava.model.definitions.reducedmodel.ReducedModelControl;
import edu.rice.cs.drjava.model.definitions.reducedmodel.HighlightStatus;
import edu.rice.cs.drjava.model.definitions.reducedmodel.IndentInfo;
import edu.rice.cs.drjava.model.definitions.reducedmodel.ReducedModelState;

import java.util.HashSet;
import java.util.Hashtable;
import java.util.StringTokenizer;
import java.util.Vector;
import javax.swing.ProgressMonitor;
import javax.swing.text.AbstractDocument;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.Position;


public abstract class AbstractDJDocument extends SwingDocument implements DJDocument, OptionConstants {
  
  
  
  
  protected static final HashSet<String> _normEndings = _makeNormEndings();
  
  protected static final HashSet<String> _keywords = _makeKeywords();
  
  protected static final HashSet<String> _primTypes = _makePrimTypes();
  
  protected int _indent = 2;
  
  
  public BraceReduction _reduced = new ReducedModelControl();  
  
  
  protected int _currentLocation = 0;
  
  
  
  
  private final Hashtable<String, Object> _helperCache = new Hashtable<String, Object>();
  
  
  private final Vector<String> _helperCacheHistory = new Vector<String>();
  
  
  protected boolean _cacheInUse;
  
  
  private static final int MAX_CACHE_SIZE = 10000;
  
  
  public static final int DOCSTART = 0;
  
  
  public static final int ERROR_INDEX = -1;
  
  
  private final Indenter _indenter;
  
  
  private OptionListener<Integer> _listener1;
  private OptionListener<Boolean> _listener2;
  
  
  
  protected AbstractDJDocument() {
    int ind = DrJava.getConfig().getSetting(INDENT_LEVEL).intValue();
    _indenter = makeNewIndenter(ind); 
    _initNewIndenter();
  }
  
  protected AbstractDJDocument(Indenter indent) { _indenter = indent; }
  
  
  
  
  protected abstract Indenter makeNewIndenter(int indentLevel);
  
  
  public int getIndent() { return _indent; }
  
  
  public void setIndent(final int indent) {
    DrJava.getConfig().setSetting(INDENT_LEVEL,new Integer(indent));
    this._indent = indent;
  }
  
  protected void _removeIndenter() {
    DrJava.getConfig().removeOptionListener(INDENT_LEVEL, _listener1);
    DrJava.getConfig().removeOptionListener(AUTO_CLOSE_COMMENTS, _listener2);
  }
  
  private void _initNewIndenter() {
    
    
    _listener1 = new OptionListener<Integer>() {
      public void optionChanged(OptionEvent<Integer> oce) {
        _indenter.buildTree(oce.value.intValue());
      }
    };
    
    _listener2 = new OptionListener<Boolean>() {
      public void optionChanged(OptionEvent<Boolean> oce) {
        _indenter.buildTree(DrJava.getConfig().getSetting(INDENT_LEVEL).intValue());
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
    
  
  public Vector<HighlightStatus> getHighlightStatus(int start, int end) {
    
    
    Vector<HighlightStatus> v;

    readLock();
    try {
      synchronized(_reduced) {
        setCurrentLocation(start);
        
        v = _reduced.getHighlightStatus(start, end - start);
        
        
        for (int i = 0; i < v.size(); i++) {
          HighlightStatus stat = v.get(i);
          if (stat.getState() == HighlightStatus.NORMAL) i = _highlightKeywords(v, i);
        }
      }
    }
    finally { readUnlock(); }
    
    
    
    
    
    
    
    
    return v;
  }
  
  
  private int _highlightKeywords(Vector<HighlightStatus> v, int i) {
    
    final String delimiters = " \t\n\r{}()[].+-/*;:=!@#$%^&*~<>?,\"`'<>|";
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
  
  
  
  private boolean _isNum(String x) {
    try {
      Double.parseDouble(x);
      return true;
    } 
    catch (NumberFormatException e) {  return false; }
  }
  
  
  private boolean _isType(String x) {
    if (_primTypes.contains(x)) return true;
    
    try { return Character.isUpperCase(x.charAt(0)); } 
    catch (IndexOutOfBoundsException e) { return false; }
  }
  
  
  private boolean _hasOnlySpaces(String text) { return (text.trim().length() == 0); }
  
  
  protected abstract void _styleChanged(); 
  
  
  protected void clearCache() {
    synchronized(_helperCache) { if (_cacheInUse) _clearCache(); }
  }
  
  
  private void _clearCache() {
    _helperCache.clear();
    _helperCacheHistory.clear();
    _cacheInUse = false;
  }
    
  
  
  private void _addCharToReducedModel(char curChar) {
    clearCache();
   _reduced.insertChar(curChar);
  }
  
  
  public int getCurrentLocation() { return  _currentLocation; }
  
  
  public void setCurrentLocation(int loc)  { 
    readLock();
    try {
      synchronized(_reduced) { 
        move(loc - _currentLocation);  
      }
    }
    finally { readUnlock(); }
  }
  
  
  public void move(int dist) {
    readLock();
    try {
      synchronized(_reduced) {
        int newLoc = _currentLocation + dist;
        
        if (newLoc < 0) newLoc = 0;
        else if (newLoc > getLength()) newLoc = getLength();
        _currentLocation = newLoc;
        _reduced.move(dist);
      }
    }
    finally { readUnlock(); }   
  }
  
  
  
  
  public int balanceBackward() {
    readLock();
    try { 
      synchronized(_reduced) { return _reduced.balanceBackward(); }
    }
    finally { readUnlock(); }  
  }
  
  
  public int balanceForward() {
    readLock();
    try { synchronized(_reduced) { return _reduced.balanceForward(); } }
    finally { readUnlock(); }  
  }
  
  
  public BraceReduction getReduced() { return _reduced; }
  
  
  public IndentInfo getIndentInformation() {
    
    String key = "getIndentInformation:" + _currentLocation;

    IndentInfo cached = (IndentInfo) _checkCache(key);
    if (cached != null) return cached;
    
    IndentInfo info;
    readLock();
    try { synchronized(_reduced) { info = _reduced.getIndentInformation(); } }
    finally { readUnlock(); }  
    _storeInCache(key, info);
    return info;
  }
  
  public ReducedModelState stateAtRelLocation(int dist) {
    readLock();
    try { synchronized(_reduced) { return _reduced.moveWalkerGetState(dist); } }
    finally { readUnlock(); }  
  }
  
  public ReducedModelState getStateAtCurrent() {
    readLock();
    try { synchronized(_reduced) { return _reduced.getStateAtCurrent(); } }
    finally { readUnlock(); } 
  }
  
  public void resetReducedModelLocation() {
    readLock();
    try { synchronized(_reduced) { _reduced.resetLocation(); } }
    finally { readUnlock(); } 
  }
  
  
  public int findPrevDelimiter(int pos, char[] delims) throws BadLocationException {
    return findPrevDelimiter(pos, delims, true);
  }
  
  
  public int findPrevDelimiter(final int pos, final char[] delims, final boolean skipParenPhrases)
    throws BadLocationException {
    
    StringBuffer keyBuf = new StringBuffer("findPrevDelimiter:").append(pos);
    for (char ch: delims) { keyBuf.append(':').append(ch); }
    keyBuf.append(':').append(skipParenPhrases);
    String key = keyBuf.toString();
    Integer cached = (Integer) _checkCache(key);
    if (cached != null) return cached.intValue();
    
    int reducedPos = pos;
    int i;  
    readLock();
    try {
      String text = getText(DOCSTART, pos);
      
      synchronized(_reduced) {
        final int origLocation = _currentLocation;
        
        _reduced.move(pos - origLocation);  
        
        
        for (i = pos-1; i >= DOCSTART; i--) {
          
          
          if (match(text.charAt(i),delims)) {
            
            _reduced.move(i - reducedPos);  
            reducedPos = i;                 
            
            
            
            ReducedModelState state = _reduced.getStateAtCurrent();
            if (!state.equals(ReducedModelState.FREE) || _isStartOfComment(text, i)
                  || ((i > 0) && _isStartOfComment(text, i - 1)) || (skipParenPhrases && posInParenPhrase()))
              continue;  
            else break;  
          }
        }
        
        
        
        _reduced.move(origLocation - reducedPos);    
      }  
    }
    finally { readUnlock(); }
    
    
    
    if (i == DOCSTART-1) reducedPos = ERROR_INDEX; 
    _storeInCache(key, new Integer(reducedPos));
    return reducedPos;  
  }
  
  private static boolean match(char c, char[] delims) {
    for (char d : delims) { if (c == d) return true; } 
    return false;
  }
        
  
  public boolean findCharInStmtBeforePos(char findChar, int position) {
    if (position == DefinitionsDocument.ERROR_INDEX) {
      String mesg = 
        "Argument endChar to QuestionExistsCharInStmt must be a char that exists on the current line.";
      throw new UnexpectedException(new IllegalArgumentException(mesg));
    }
    
    char[] findCharDelims = {findChar, ';', '{', '}'};
    int prevFindChar;
    
    
    boolean found;
    
    readLock();
    try {
      prevFindChar = this.findPrevDelimiter(position, findCharDelims, false);
      
      if ((prevFindChar == DefinitionsDocument.ERROR_INDEX) || (prevFindChar < 0)) return false; 
      
      
      String foundString = this.getText(prevFindChar, 1);
      char foundChar = foundString.charAt(0);
      found = (foundChar == findChar);
    }
    catch (Throwable t) { throw new UnexpectedException(t); }
    finally { readUnlock(); }
    return found;
  }
  
  
  public int findPrevCharPos(int pos, char[] whitespace) throws BadLocationException {
    
    StringBuffer keyBuf = new StringBuffer("findPrevCharPos:").append(pos);
    for (char ch: whitespace) { keyBuf.append( ':').append(ch); }
    String key = keyBuf.toString();
    Integer cached = (Integer) _checkCache(key);
    if (cached != null)  return cached.intValue();
    
    int reducedPos = pos;
    int i = pos - 1;
    String text;
    readLock();
    try { 
      text = getText(0, pos); 
      
      synchronized(_reduced) {
        
        final int origLocation = _currentLocation;
        
        _reduced.move(pos - origLocation);  
        
        
        
        while (i >= 0) { 
          
          
          if (match(text.charAt(i), whitespace)) {
            
            i--;
            continue;
          }
          
          
          _reduced.move(i - reducedPos);
          reducedPos = i;                  
          
          
          if ((_reduced.getStateAtCurrent().equals(ReducedModelState.INSIDE_LINE_COMMENT)) ||
              (_reduced.getStateAtCurrent().equals(ReducedModelState.INSIDE_BLOCK_COMMENT))) {
            i--;
            continue;
          }
          
          if (_isEndOfComment(text, i)) {   
            
            i = i - 2;
            continue;
          }
          
          
          break;
        }
        
        
        _reduced.move(origLocation - reducedPos);
      }
    }
    finally { readUnlock(); }
    int result = reducedPos;
    if (i < 0) result = ERROR_INDEX;
    _storeInCache(key, new Integer(result));
    return result;
  }
  
  
  protected Object _checkCache(String key) {
    
    Object result = _helperCache.get(key);  
    
    return result;
  }
  
  
  protected void _storeInCache(String key, Object result) {
    synchronized(_helperCache) {
      _cacheInUse = true;
      
      
      if (_helperCache.size() >= MAX_CACHE_SIZE) {
        if (_helperCacheHistory.size() > 0) {
          _helperCache.remove( _helperCacheHistory.get(0) );
          _helperCacheHistory.remove(0);
        }
        else { 
          throw new RuntimeException("Cache larger than cache history!");
        }
      }
      Object prev = _helperCache.put(key, result);
      
      if (prev == null) _helperCacheHistory.add(key);
    }
  }
  
  
  public void indentLines(int selStart, int selEnd) {
    try { indentLines(selStart, selEnd, Indenter.OTHER, null); }
    catch (OperationCanceledException oce) {
      
      throw new UnexpectedException(oce);
    }
  }
  
  
  
  public void indentLines(int selStart, int selEnd, int reason, ProgressMonitor pm)
    throws OperationCanceledException {
    
    
    
    
    writeLock();
    try {
      synchronized(_reduced) {
        if (selStart == selEnd) {  

          Position oldCurrentPosition = createPosition(_currentLocation);
          
          

          if (_indentLine(reason)) {
            setCurrentLocation(oldCurrentPosition.getOffset());
            if (onlyWhiteSpaceBeforeCurrent()) {
              int space = getWhiteSpace();
              move(space);
            }
          }
        }
        else _indentBlock(selStart, selEnd, reason, pm);
      }
    }
    catch (Throwable t) { throw new UnexpectedException(t); }
    finally { writeUnlock(); } 
    
    
    
    endLastCompoundEdit();
  }
  
  
  private void _indentBlock(final int start, final int end, int reason, ProgressMonitor pm)
    throws OperationCanceledException, BadLocationException {
    
    
    
    final Position endPos = this.createPosition(end);
    
    int walker = start;
    while (walker < endPos.getOffset()) {
      setCurrentLocation(walker);
      
      
      Position walkerPos = this.createPosition(walker);
      
      
      _indentLine(reason);  
      
      setCurrentLocation(walkerPos.getOffset());
      walker = walkerPos.getOffset();
      
      if (pm != null) {
        pm.setProgress(walker); 
        if (pm.isCanceled()) throw new OperationCanceledException(); 
      }
      
      
      
      walker += _reduced.getDistToNextNewline() + 1;
    }
  }
  
  
  public boolean _indentLine(int reason) { return _indenter.indent(this, reason); }
  
  
  public int getIntelligentBeginLinePos(int currPos) throws BadLocationException {
    String prefix;
    int firstChar;
    readLock();
    try {
      firstChar = getLineStartPos(currPos);
      prefix = getText(firstChar, currPos-firstChar);
    }
    finally { readUnlock(); }
    
    
    int i;
    int len = prefix.length();
   
    for (i = 0; i < len; i++ ) { if (! Character.isWhitespace(prefix.charAt(i))) break; }

    
    if (i < len) {
      int firstRealChar = firstChar + i;
      if (firstRealChar < currPos) return firstRealChar;
    }
    
    return firstChar;
  }
  
  
  public String getIndentOfCurrStmt(int pos) throws BadLocationException {
    char[] delims = {';', '{', '}'};
    char[] whitespace = {' ', '\t', '\n',','};
    return getIndentOfCurrStmt(pos, delims, whitespace);
  }
  
  
  public String getIndentOfCurrStmt(int pos, char[] delims) throws BadLocationException {
    char[] whitespace = {' ', '\t', '\n',','};
    return getIndentOfCurrStmt(pos, delims, whitespace);
  }
  
  
  public String getIndentOfCurrStmt(int pos, char[] delims, char[] whitespace) throws BadLocationException {
    
    StringBuffer keyBuf = new StringBuffer("getIndentOfCurrStmt:").append(pos);
    for (char ch: delims) { keyBuf.append(':').append(ch); }
    String key = keyBuf.toString();
    String cached = (String) _checkCache(key);
    if (cached != null) return cached;
    
    String lineText;
    
    readLock();
    try {
      synchronized(_reduced) {
        
        int lineStart = getLineStartPos(pos);
        
        
        boolean reachedStart = false;
        int prevDelim = lineStart;
        boolean ignoreParens = posInParenPhrase(prevDelim);
        
        do {
          prevDelim = findPrevDelimiter(prevDelim, delims, ignoreParens);
          if (prevDelim > 0 && prevDelim < getLength() && getText(prevDelim,1).charAt(0) == '{') break;
          if (prevDelim == ERROR_INDEX) { 
            reachedStart = true;
            break;
          }
          ignoreParens = posInParenPhrase(prevDelim);
        } while (ignoreParens);  
    
        
        int nextNonWSChar;
        if (reachedStart) nextNonWSChar = getFirstNonWSCharPos(DOCSTART);
        else nextNonWSChar = getFirstNonWSCharPos(prevDelim+1, whitespace, false);
        
        
        if (nextNonWSChar == ERROR_INDEX) nextNonWSChar = getLength();
        
        
        int lineStartStmt = getLineStartPos(nextNonWSChar);
        
        
        int lineFirstNonWS = getLineFirstCharPos(lineStartStmt);
        lineText = getText(lineStartStmt, lineFirstNonWS - lineStartStmt); 
      }
    }
    catch(Throwable t) { throw new UnexpectedException(t); }
    finally { readUnlock(); }
    
    _storeInCache(key, lineText);
    return lineText;
  }
  
  
  public int findCharOnLine(int pos, char findChar) {
    
    String key = "findCharOnLine:" + pos + ":" + findChar;
    Integer cached = (Integer) _checkCache(key);
    if (cached != null) return cached.intValue();
    
    int i;
    int matchIndex; 
    
    readLock();
    try {
      synchronized(_reduced) {
        int here = _currentLocation;
        int lineStart = getLineStartPos(pos);
        int lineEnd = getLineEndPos(pos);
        String lineText = getText(lineStart, lineEnd - lineStart);
        i = lineText.indexOf(findChar, 0);
        matchIndex = i + lineStart;
        
        while (i != -1) { 
          
          
          
          _reduced.move(matchIndex - here);  
          
          
          if (_reduced.getStateAtCurrent().equals(ReducedModelState.FREE)) {
            
            _reduced.move(here - matchIndex);  
            break;
          } 
          
          
          _reduced.move(here - matchIndex);  
          i = lineText.indexOf(findChar, i+1);
        }
      }
    }
    catch (Throwable t) { throw new UnexpectedException(t); }
    finally { readUnlock(); }
    
    if (i == -1) matchIndex = ERROR_INDEX;
    _storeInCache(key, new Integer(matchIndex));
    return matchIndex;
  }
  
  
  public int getLineStartPos(final int pos) {
    if (pos < 0 || pos > getLength()) return -1;
    
    String key = "getLineStartPos:" + pos;
    Integer cached = (Integer) _checkCache(key);
    if (cached != null) return cached.intValue();
    
    int dist;
    readLock();
    try {
      synchronized(_reduced) {
        int location = _currentLocation;
        _reduced.move(pos - location);
        dist = _reduced.getDistToPreviousNewline(0);
        _reduced.move(location - pos);
      }
    }
    finally { readUnlock(); }
    
    if (dist == -1) {
      
      _storeInCache(key, new Integer(DOCSTART));
      return DOCSTART;
    }
    
    _storeInCache(key, new Integer(pos - dist));
    return pos - dist;
  }
  
  
  public int getLineEndPos(final int pos) {
    if (pos < 0 || pos > getLength()) return -1;
    
    
    String key = "getLineEndPos:" + pos;
    Integer cached = (Integer) _checkCache(key);
    if (cached != null) return cached.intValue();
    
    int dist;
    readLock();
    try {
      synchronized(_reduced) {
        int location = _currentLocation;
        _reduced.move(pos - location);
        dist = _reduced.getDistToNextNewline();
        _reduced.move(location - pos);
      }
    }
    finally { readUnlock(); }
    _storeInCache(key, new Integer(pos + dist));
    return pos + dist;
  }
  
  
  public int getLineFirstCharPos(int pos) throws BadLocationException {
    
    String key = "getLineFirstCharPos:" + pos;
    Integer cached = (Integer) _checkCache(key);
    if (cached != null)  return cached.intValue();
    
    int startLinePos = getLineStartPos(pos);
    int endLinePos = getLineEndPos(pos);
    
    
    String text = this.getText(startLinePos, endLinePos - startLinePos);
    int walker = 0;
    while (walker < text.length()) {
      if (text.charAt(walker) == ' ' ||
          text.charAt(walker) == '\t') {
        walker++;
      }
      else {
        _storeInCache(key, new Integer(startLinePos + walker));
        return startLinePos + walker;
      }
    }
    
    _storeInCache(key, new Integer(endLinePos));
    return endLinePos;
  }
  
  
  public int getFirstNonWSCharPos(int pos) throws BadLocationException {
    char[] whitespace = {' ', '\t', '\n'};
    return getFirstNonWSCharPos(pos, whitespace, false);
  }
  
  
  public int getFirstNonWSCharPos(int pos, boolean acceptComments) throws BadLocationException {
    char[] whitespace = {' ', '\t', '\n'};
    return getFirstNonWSCharPos(pos, whitespace, acceptComments);
  }
  
  
  public int getFirstNonWSCharPos(int pos, char[] whitespace, boolean acceptComments) throws BadLocationException {
    
    StringBuffer keyBuf = new StringBuffer("getFirstNonWSCharPos:").append(pos);
    for (char ch: whitespace) { keyBuf.append(':').append(ch); }
    String key = keyBuf.toString();
    
    Integer cached = (Integer) _checkCache(key);
    if (cached != null)  return cached.intValue();
    
    int i = pos;
    int endPos = getLength();
    
    
    String text = getText(pos, endPos - pos);
    
    final int origLocation = _currentLocation;
    
    _reduced.move(pos - origLocation);
    int reducedPos = pos;
    
    
    
    
    while (i < endPos) {
      
      
      if (match(text.charAt(i-pos), whitespace)) {
        i++;
        continue;
      }
      
      
      _reduced.move(i - reducedPos);  
      reducedPos = i;                 
      
      
      if (! acceptComments &&
          ((_reduced.getStateAtCurrent().equals(ReducedModelState.INSIDE_LINE_COMMENT)) ||
           (_reduced.getStateAtCurrent().equals(ReducedModelState.INSIDE_BLOCK_COMMENT)))) {
        i++;
        continue;
      }
      
      
      if (! acceptComments && _isStartOfComment(text, i - pos)) {
        
        
        i = i + 2;
        continue;
      }
      
      
      break;
    }
    _reduced.move(origLocation - reducedPos);
    
    int result = reducedPos;
    if (i == endPos) result = ERROR_INDEX;
    
    _storeInCache(key, new Integer(result));
    return result;
  }
  
  public int findPrevNonWSCharPos(int pos) throws BadLocationException {
    char[] whitespace = {' ', '\t', '\n'};
    return findPrevCharPos(pos, whitespace);
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

  
  protected static boolean _isEndOfComment(String text, int pos) {
    char currChar = text.charAt(pos);
    if (currChar == '/') {
      try {
        char beforeCurrChar = text.charAt(pos - 1);
        if ((beforeCurrChar == '/') || (beforeCurrChar == '*'))  return true;
      } catch (StringIndexOutOfBoundsException e) {  }
    }
    return false;
  }
  
  
  
  public boolean posInParenPhrase(int pos) {
    
    String key = "posInParenPhrase:" + pos;
    Boolean cached = (Boolean) _checkCache(key);
    if (cached != null) return cached.booleanValue();

    boolean inParenPhrase;
    
    readLock();
    try {
      synchronized(_reduced) {
        int here = _currentLocation;
        _reduced.move(pos - here);
        inParenPhrase = posInParenPhrase();
        _reduced.move(here - pos);
      }
    }
    finally { readUnlock(); }

    _storeInCache(key, Boolean.valueOf(inParenPhrase));
    return inParenPhrase;
  }

  
  public boolean posInParenPhrase() {
    IndentInfo info;
    readLock();
    try { synchronized(_reduced) { info = _reduced.getIndentInformation(); } }
    finally { readUnlock(); }
    return info.braceTypeCurrent.equals(IndentInfo.openParen);
  }

  
  protected boolean posNotInBlock(int pos) {
    
    String key = "posNotInBlock:" + pos;
    Boolean cached = (Boolean) _checkCache(key);
    if (cached != null) return cached.booleanValue();
    
    boolean notInParenPhrase;
    
    synchronized(_reduced) {
      int here = _currentLocation;
      _reduced.move(pos - here);
      IndentInfo info = _reduced.getIndentInformation();
      notInParenPhrase = info.braceTypeCurrent.equals(IndentInfo.noBrace);
      _reduced.move(here - pos);
    }
    _storeInCache(key, Boolean.valueOf(notInParenPhrase));
    return notInParenPhrase;
  }
  
  
  
  public int getWhiteSpace() {
    try { return  getWhiteSpaceBetween(0, getLength() - _currentLocation); } 
    catch (BadLocationException e) { e.printStackTrace(); }
    return  -1;
  }

  
  private int getWhiteSpaceBetween(int relStart, int relEnd) throws BadLocationException {
    String text = this.getText(_currentLocation - relStart, Math.abs(relStart - relEnd));
    int i = 0;
    int length = text.length();
    while ((i < length) && (text.charAt(i) == ' '))
      i++;
    return  i;
  }
  
  
  private boolean onlyWhiteSpaceBeforeCurrent() throws BadLocationException{
    String text = this.getText(0, _currentLocation);
    
    text = text.substring(text.lastIndexOf("\n")+1);
    
    
    int index = text.length()-1;
    char lastChar = ' ';
    while(lastChar == ' ' && index >= 0){
      lastChar = text.charAt(index);
      index--;
    }
    
    if (index < 0) return true;
    return false;
  }
      
                          
  
  
  public void setTab(String tab, int pos) {
    try {
      int startPos = getLineStartPos(pos);
      int firstNonWSPos = getLineFirstCharPos(pos);
      int len = firstNonWSPos - startPos;

      
      boolean onlySpaces = _hasOnlySpaces(tab);
      if (!onlySpaces || (len != tab.length())) {

        if (onlySpaces) {
          
          int diff = tab.length() - len;
          if (diff > 0) {
            insertString(firstNonWSPos, tab.substring(0, diff), null);
          }
          else {
            remove(firstNonWSPos + diff, -diff);
          }
        }
        else {
          
          remove(startPos, len);
          insertString(startPos, tab, null);
        }
      }
    }
    catch (BadLocationException e) {
      
      throw new UnexpectedException(e);
    }
  }
  
  
  protected void insertUpdate(AbstractDocument.DefaultDocumentEvent chng, AttributeSet attr) {
    
    clearCache();

    super.insertUpdate(chng, attr);

    try {
      final int offset = chng.getOffset();
      final int length = chng.getLength();
      final String str = getText(offset, length);

      InsertCommand doCommand = new InsertCommand(offset, str);
      RemoveCommand undoCommand = new RemoveCommand(offset, length);

      
      addUndoRedo(chng,undoCommand,doCommand);
      
      
      doCommand.run();
    }
    catch (BadLocationException ble) {
      throw new UnexpectedException(ble);
    }
  }
  
  
  protected void removeUpdate(AbstractDocument.DefaultDocumentEvent chng) {
    clearCache();

    try {
      final int offset = chng.getOffset();
      final int length = chng.getLength();
      final String removedText = getText(offset, length);
      super.removeUpdate(chng);

      Runnable doCommand = new RemoveCommand(offset, length);
      Runnable undoCommand = new InsertCommand(offset, removedText);

      
      addUndoRedo(chng,undoCommand,doCommand);
      
      doCommand.run();
    }
    catch (BadLocationException e) { throw new UnexpectedException(e); }
  }
  
  
  
  public void insertString(int offset, String str, AttributeSet a) throws BadLocationException {
    
    writeLock();
    try {
      synchronized(_reduced) {    
        clearCache();      
        super.insertString(offset,str,a);
      }
    }
    finally { writeUnlock(); }
  }
  
  
  public void remove(int offset, int len) throws BadLocationException {
    
    writeLock();
    try {
      synchronized(_reduced) {
        clearCache();     
        super.remove(offset, len);
      }
    }
    finally { writeUnlock(); }  
  }
    
  public String getText() {
    readLock();
    try { return getText(0, getLength()); }
    catch(BadLocationException e) { throw new UnexpectedException(e); }
    finally { readUnlock(); }
  }
  
  public void clear() {
    writeLock();
    try { remove(0, getLength()); }
    catch(BadLocationException e) { throw new UnexpectedException(e); }
    finally { writeUnlock(); }
  }
   
  
  protected abstract int startCompoundEdit();
  protected abstract void endCompoundEdit(int i);
  protected abstract void endLastCompoundEdit();
  protected abstract void addUndoRedo(AbstractDocument.DefaultDocumentEvent chng, Runnable undoCommand, Runnable doCommand);
  
  
  
  
  
  protected class InsertCommand implements Runnable {
    private final int _offset;
    private final String _text;
    
    public InsertCommand(final int offset, final String text) {
      _offset = offset;
      _text = text;
    }
    
    public void run() {
      
      readLock();
      try {
        synchronized(_reduced) {
          _reduced.move(_offset - _currentLocation);  
          int len = _text.length();
          
          for (int i = 0; i < len; i++) {
            char curChar = _text.charAt(i);
            _addCharToReducedModel(curChar);
          }
          _currentLocation = _offset + len;  
          _styleChanged();
        }
      }
      finally { readUnlock(); }
    }
  }

  protected class RemoveCommand implements Runnable {
    private final int _offset;
    private final int _length;
    
    public RemoveCommand(final int offset, final int length) {
      _offset = offset;
      _length = length;
    }
    
    public void run() {
      readLock();
      try {
        synchronized(_reduced) { 
          setCurrentLocation(_offset);
          _reduced.delete(_length);    
          _styleChanged();
        }
      }
      finally { readUnlock(); } 
    }
  }
}
