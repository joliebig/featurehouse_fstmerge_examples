

package edu.rice.cs.drjava.model.definitions;

import javax.swing.text.*;
import javax.swing.undo.*;
import javax.swing.event.DocumentEvent;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.WeakHashMap;
import java.lang.ref.WeakReference;

import java.io.File;

import edu.rice.cs.drjava.model.definitions.reducedmodel.*;
import edu.rice.cs.util.UnexpectedException;
import edu.rice.cs.util.swing.Utilities;
import edu.rice.cs.drjava.model.definitions.indent.Indenter;
import edu.rice.cs.drjava.model.OpenDefinitionsDocument;
import edu.rice.cs.drjava.model.*;



public class DefinitionsDocument extends AbstractDJDocument implements Finalizable<DefinitionsDocument> {
  
  private final static int NO_COMMENT_OFFSET = 0;
  private final static int WING_COMMENT_OFFSET = 2;
  
  List<DocumentClosedListener> _closedListeners = new LinkedList<DocumentClosedListener>();
  
  public void addDocumentClosedListener(DocumentClosedListener l) { 
    synchronized(_closedListeners) { _closedListeners.add(l); }
  }
  
  public void removeDocumentClosedListener(DocumentClosedListener l) { 
    synchronized(_closedListeners) { _closedListeners.remove(l); }
  }
  
  
  





  
  
  public void close() {
    _removeIndenter();
    
    synchronized(_closedListeners) {
      for (DocumentClosedListener l: _closedListeners)  { l.close(); }
      _closedListeners = new LinkedList<DocumentClosedListener>();
    }
  }
  
  
  
  
  
  private static final int UNDO_LIMIT = 1000;
  
  private static boolean _tabsRemoved = true;
  
  private volatile boolean _isModifiedSinceSave = false;
  
  private volatile boolean _classFileInSync = false;
  
  private volatile int _cachedLocation;
  
  private volatile int _cachedLineNum;
  
  private volatile int _cachedPrevLineLoc;
  
  private volatile int _cachedNextLineLoc;

  
  private volatile String _packageName;
  
  private volatile File _classFile;

  
  private volatile OpenDefinitionsDocument _odd;
  
  private CompoundUndoManager _undoManager;
  
  
  private final GlobalEventNotifier _notifier;
  
  
  private LinkedList<WeakReference<WrappedPosition>> _wrappedPosList;
  
  
  public DefinitionsDocument(Indenter indenter, GlobalEventNotifier notifier) {
    super(indenter);
    _notifier = notifier;
    _init();
    resetUndoManager();
  }

  
  public DefinitionsDocument(GlobalEventNotifier notifier) {
    super();
    _notifier = notifier;
    _init();
    resetUndoManager();
  }

  
  public DefinitionsDocument(GlobalEventNotifier notifier, CompoundUndoManager undoManager) {
    super();
    _notifier = notifier;
    _init();
    _undoManager = undoManager;
  }
  
  





  
  protected Indenter makeNewIndenter(int indentLevel) { return new Indenter(indentLevel); }
  
  
  private void _init() {
    _odd = null;
    _cachedLocation = 0;
    _cachedLineNum = 1;
    _cachedPrevLineLoc = -1;
    _cachedNextLineLoc = -1;
    _classFile = null;
    _cacheInUse = false;
  }
  
  
  
 











   
  
  public void setOpenDefDoc(OpenDefinitionsDocument odd) { if (_odd == null) _odd = odd; }
  
  
  public OpenDefinitionsDocument getOpenDefDoc() {
    if (_odd == null)
      throw new IllegalStateException("The OpenDefinitionsDocument for this DefinitionsDocument has never been set");
    else return _odd;
  }
  
  protected void _styleChanged() {    
    modifyLock();
    try {
      int length = getLength() - _currentLocation;
      
      
      DocumentEvent evt = new DefaultDocumentEvent(_currentLocation, length, DocumentEvent.EventType.CHANGE);
      fireChangedUpdate(evt);
    }
    finally { modifyUnlock(); }
  } 
  
  
  




  



















































  
  public String getQualifiedClassName() throws ClassNameNotFoundException {
    return _getPackageQualifier() + getFirstTopLevelClassName();
  }

  
  public String getQualifiedClassName(int pos) throws ClassNameNotFoundException {
    return _getPackageQualifier() + getEnclosingTopLevelClassName(pos);
  }

  
  protected String _getPackageQualifier() {
    String packageName = "";
    try { packageName = getPackageName(); }
    catch (InvalidPackageException e) { 
       
    }
    if ((packageName != null) && (!packageName.equals(""))) { packageName = packageName + "."; }
    return packageName;
  }

  public void setClassFileInSync(boolean inSync) { _classFileInSync = inSync; }

  public boolean getClassFileInSync() { return _classFileInSync; }

  public void setCachedClassFile(File classFile) { _classFile = classFile; }

  public File getCachedClassFile() { return _classFile; }

  
  public void insertString(int offset, String str, AttributeSet a) throws BadLocationException {
    
    
    
    
    
    
    modifyLock();
    try {
      if (_tabsRemoved) str = _removeTabs(str);
      setModifiedSinceSave();
      super.insertString(offset, str, a);
    }
    finally { modifyUnlock(); }
  }
  
  
  
  public void remove(int offset, int len) throws BadLocationException {
    
    if (len == 0) return;
    
    modifyLock();
    try {
      setModifiedSinceSave();
      super.remove(offset, len);
    }
    finally { modifyUnlock(); }
  }

  
  static String _removeTabs(final String source) {

    return source.replace('\t', ' ');
  }

  
  public void updateModifiedSinceSave() {
    
    modifyLock();
    try {
    _isModifiedSinceSave = _undoManager.isModified();

    }
    finally { 
      if (! _isModifiedSinceSave && _odd != null) _odd.documentReset();
      modifyUnlock();

    }
  }
  
   
  private void setModifiedSinceSave() {
    if (! _isModifiedSinceSave) {
      _isModifiedSinceSave = true;
      _classFileInSync = false;
      if (_odd != null) _odd.documentModified();
    }    
  }
  
  
  public void resetModification() {
    modifyLock();
    try {
      _isModifiedSinceSave = false;
      _undoManager.documentSaved();
    }
    finally { 
      if (_odd != null) _odd.documentReset();  
      modifyUnlock(); 

    }
  }
  
  
  public boolean isModifiedSinceSave() {
    readLock();
    try { return  _isModifiedSinceSave; }
    finally { readUnlock(); }
  }
  
  
  public int getCurrentCol() {
    
    int here = _currentLocation;
    int startOfLine = getLineStartPos(here);
    return here - startOfLine;
  }

  
  public int getCurrentLine() {
    
    int here = _currentLocation;
    if (_cachedLocation > getLength()) {
      
      _cachedLocation = 0;
      _cachedLineNum = 1;
    }
    if (_cachedNextLineLoc > getLength()) _cachedNextLineLoc = -1;
    
    if ( ! (_cachedPrevLineLoc < here && here < _cachedNextLineLoc )) {

      
      
      if (_cachedLocation - here > here) {
        _cachedLocation = 0;
        _cachedLineNum = 1;
      }
      int lineOffset = _getRelativeLine();
      _cachedLineNum = _cachedLineNum + lineOffset;

    }
    _cachedLocation = here;
    _cachedPrevLineLoc = getLineStartPos(here);
    _cachedNextLineLoc = getLineEndPos(here);
    return _cachedLineNum;
  }

  
  private int _getRelativeLine() {
    
    int count = 0;
    int currLoc = _currentLocation;
    setCurrentLocation(_cachedLocation);

    if (_cachedLocation > currLoc) {
      
      int prevLineLoc = getLineStartPos( _cachedLocation );
      while (prevLineLoc > currLoc) {
        count--;
        prevLineLoc = getLineStartPos( prevLineLoc - 1 );
        
        setCurrentLocation(prevLineLoc);
      }
    }

    else {
      
      int nextLineLoc = getLineEndPos( _cachedLocation );
      while (nextLineLoc < currLoc) {
        count++;
        nextLineLoc = getLineEndPos( nextLineLoc + 1 );
        
        setCurrentLocation(nextLineLoc);
      }
    }
    setCurrentLocation(currLoc);
    return count;
  }

  
  public int getOffset(int lineNum) {
    if (lineNum < 0) return -1;
    String defsText = getText();
    int curLine = 1;
    int offset = 0; 
    
    
    
    while (offset < defsText.length()) {
      
      if (curLine==lineNum) return offset;
      
      int nextNewline = defsText.indexOf('\n', offset);
      if (nextNewline == -1) return -1; 
      
      curLine++;
      offset = nextNewline + 1;
    }
    return -1;
  }

  
  public boolean tabsRemoved() { return _tabsRemoved; }
 
  
  public int commentLines(int selStart, int selEnd) {
    
    
    int toReturn = selEnd;
    if (selStart == selEnd) {
      modifyLock();
      try {     
        synchronized(_reduced) {
          setCurrentLocation(selStart);
          Position oldCurrentPosition = createPosition(_currentLocation);
          _commentLine();   
          toReturn += WING_COMMENT_OFFSET;
          
          
        }
      }
      catch (BadLocationException e) { throw new UnexpectedException(e); }
      finally { modifyUnlock(); }
    }
    else toReturn = _commentBlock(selStart, selEnd);   
    _undoManager.endLastCompoundEdit();  
    return toReturn;
  }
 

  
  private int _commentBlock(final int start, final int end) {
    int afterCommentEnd = end;
    modifyLock();
    try {
      
      
      final Position endPos = this.createPosition(end);
      
      int walker = start;
      synchronized(_reduced) {
        while (walker < endPos.getOffset()) {
          setCurrentLocation(walker);
          
          
          Position walkerPos = this.createPosition(walker);
          
          _commentLine();  
          afterCommentEnd += WING_COMMENT_OFFSET;
          
          setCurrentLocation(walkerPos.getOffset());
          walker = walkerPos.getOffset();
          
          
          
          walker += _reduced.getDistToNextNewline() + 1;
          
        }
      }
    } 
    catch (BadLocationException e) { throw new UnexpectedException(e); }
    finally { modifyUnlock(); }
    return afterCommentEnd;
  }

  
  private void _commentLine() {
    
    
    try { insertString(_currentLocation - getCurrentCol(), "//", null); }
    catch (BadLocationException e) { throw new UnexpectedException(e); }
  }

  
  public int uncommentLines(int selStart, int selEnd) {
 
    
    int toReturn = selEnd;
    if (selStart == selEnd) {
      modifyLock();
      try {
        synchronized(_reduced) {
          setCurrentLocation(selStart);
          Position oldCurrentPosition = createPosition(_currentLocation);
          _uncommentLine();
          toReturn -= WING_COMMENT_OFFSET;
          
          
          
        }
      }
      catch (BadLocationException e) { throw new UnexpectedException(e); }
      finally { modifyUnlock(); }
    }
    else  toReturn = _uncommentBlock(selStart, selEnd);
    
    _undoManager.endLastCompoundEdit();
    return toReturn;
  }

  
  private int _uncommentBlock(final int start, final int end) {
    int afterUncommentEnd = end;
    modifyLock();
    try {
      
      
      final Position endPos = this.createPosition(end);
      
      int walker = start;
      synchronized(_reduced) {
        while (walker < endPos.getOffset()) {
          setCurrentLocation(walker);
          
          
          Position walkerPos = this.createPosition(walker);
          
          afterUncommentEnd-= _uncommentLine();
          
          setCurrentLocation(walkerPos.getOffset());
          walker = walkerPos.getOffset();
          
          
          
          walker += _reduced.getDistToNextNewline() + 1;
          
        }
      }
    }
    catch (BadLocationException e) { throw new UnexpectedException(e); }
    finally { modifyUnlock(); }
    return afterUncommentEnd;
  }

  
  private int _uncommentLine() throws BadLocationException {
    
    int curCol = getCurrentCol();
    int lineStart = _currentLocation - curCol;
    String text = getText(lineStart, curCol + _reduced.getDistToNextNewline());
    int pos = text.indexOf("//");
    
    
    
    
    
    boolean goodWing = true;
    for (int i = pos-1; i >= 0; i--) {
      char c = text.charAt(i);
      
      if (c != ' ') {
        goodWing = false;
        return NO_COMMENT_OFFSET;
      }
    }
    
    
    
    if (pos >= 0 && goodWing) {
      
      remove(lineStart + pos, 2);
      
      return WING_COMMENT_OFFSET;
    }
    return NO_COMMENT_OFFSET;
  }

  
  public void gotoLine(int line) {

    int dist;
    if (line < 0) return;
    int actualLine =1;
    
    readLock();
    int len = getLength();
    try {
      synchronized(_reduced) {
        setCurrentLocation(0);
        for (int i = 1; (i < line) && (_currentLocation < len); i++) {
          dist = _reduced.getDistToNextNewline();
          if (_currentLocation + dist < len) dist++;
          actualLine++;
          move(dist);
        }
        _cachedLineNum = actualLine;
        _cachedLocation = _currentLocation;
        _cachedPrevLineLoc = getLineStartPos(_currentLocation);
        _cachedNextLineLoc = getLineEndPos(_currentLocation);
      }
    }
    finally { readUnlock(); }
  }  
  
  private int _findNextOpenSquiggly(String text, int pos) throws BadLocationException {
    
    int i;
    int reducedPos = pos;
    
    synchronized(_reduced) {
      final int origLocation = _currentLocation;
      
      _reduced.move(pos - origLocation);  
      
      
      i = text.indexOf('{', reducedPos);
      while(i>-1) {
        
        _reduced.move(i - reducedPos);  
        reducedPos = i;                 
        
        
        ReducedModelState state = _reduced.getStateAtCurrent();
        if (!state.equals(ReducedModelState.FREE) || _isStartOfComment(text, i)
              || ((i > 0) && _isStartOfComment(text, i - 1))) {
          i = text.indexOf('{', reducedPos+1);
          continue;  
        }
        else {
          break; 
        }        
      }  
      
      _reduced.move(origLocation - reducedPos);    
    }
    
    if (i == -1) reducedPos = ERROR_INDEX; 
    return reducedPos;  
  }
  
  private int _findPrevKeyword(String text, String kw, int pos) throws BadLocationException {
    
    int i;
    int reducedPos = pos;
    
    synchronized(_reduced) {
      final int origLocation = _currentLocation;
      
      _reduced.move(pos - origLocation);  
      
      
      i = text.lastIndexOf(kw, reducedPos);
      while(i >- 1) {
        
        if (i > 0) {
          if (Character.isJavaIdentifierPart(text.charAt(i-1))) {
            
            i = text.lastIndexOf(kw, i-1);
            continue;  
          }
        }
        
        if (i+kw.length()<text.length()) {
          if (Character.isJavaIdentifierPart(text.charAt(i+kw.length()))) {
            
            i = text.lastIndexOf(kw, i-1);
            continue;  
          }
        }
        
        
        _reduced.move(i - reducedPos);  
        reducedPos = i;                 
        
        
        ReducedModelState state = _reduced.getStateAtCurrent();
        if (!state.equals(ReducedModelState.FREE) || _isStartOfComment(text, i)
              || ((i > 0) && _isStartOfComment(text, i - 1))) {
          i = text.lastIndexOf(kw, reducedPos-1);
          continue;  
        }
        else {
          break; 
        }        
      }  
      
      _reduced.move(origLocation - reducedPos);    
    }
    
    if (i == -1) reducedPos = ERROR_INDEX; 
    return reducedPos;  
  }
 

  
  
  public String getEnclosingClassName(int pos, boolean qual) throws BadLocationException, ClassNameNotFoundException {    

    
    StringBuffer keyBuf = new StringBuffer("getEnclosingClassName:").append(pos);
    keyBuf.append(":").append(qual);
    String key = keyBuf.toString();
    String cached = (String) _checkCache(key);
    if (cached != null) return cached;

    char[] delims = {'{','}','(',')','[',']','+','-','/','*',';',':','=',
      '!','@','#','$','%','^','~','\\','"','`','|'};
    String name = "";

    readLock();
    try {
      String text = getText(DOCSTART, pos+1);
      
      int curPos = pos;
      
      do {
        if ((text.charAt(curPos)!='{') || (text.charAt(curPos)!='}')) { ++curPos; }
        


        
        curPos = findPrevEnclosingBrace(curPos, '{', '}');
        if (curPos==ERROR_INDEX) { break; }
        int classPos = _findPrevKeyword(text, "class", curPos);
        int interPos = _findPrevKeyword(text, "interface", curPos);
        int otherPos = findPrevDelimiter(curPos, delims);
        int newPos = ERROR_INDEX;
        
        int closeParenPos = findPrevNonWSCharPos(curPos);
        if ((closeParenPos!=ERROR_INDEX) && (text.charAt(closeParenPos)==')')) {
          
          int openParenPos = findPrevEnclosingBrace(closeParenPos, '(', ')');
          if ((openParenPos!=ERROR_INDEX) && (text.charAt(openParenPos)=='(')) {
            
            newPos = _findPrevKeyword(text, "new", openParenPos);

            if (!_isAnonymousInnerClass(newPos, curPos)) {
              
              newPos = ERROR_INDEX;
            }
          }
        }



        while((classPos!=ERROR_INDEX) || (interPos!=ERROR_INDEX) || (newPos!=ERROR_INDEX)) {
          if (newPos!=ERROR_INDEX) {

            classPos = ERROR_INDEX;
            interPos = ERROR_INDEX;
            break;
          }
          else if ((otherPos>classPos) && (otherPos>interPos)) {
            if ((text.charAt(otherPos)!='{') || (text.charAt(otherPos)!='}')) { ++otherPos; }
            curPos = findPrevEnclosingBrace(otherPos, '{', '}');
            classPos = _findPrevKeyword(text, "class", curPos);
            interPos = _findPrevKeyword(text, "interface", curPos);
            otherPos = findPrevDelimiter(curPos, delims);
            newPos = ERROR_INDEX;
            
            closeParenPos = findPrevNonWSCharPos(curPos);


            if ((closeParenPos!=ERROR_INDEX) && (text.charAt(closeParenPos)==')')) {
              
              int openParenPos = findPrevEnclosingBrace(closeParenPos, '(', ')');
              if ((openParenPos!=ERROR_INDEX) && (text.charAt(openParenPos)=='(')) {
                
                newPos = _findPrevKeyword(text, "new", openParenPos);

                if (_isAnonymousInnerClass(newPos, curPos)) {
                  
                }
                else {
                  newPos = ERROR_INDEX;
                }
              }
            }



          }
          else {
            
            curPos = Math.max(classPos, Math.max(interPos, newPos));
            break;
          }
        }
        
        if ((classPos!=ERROR_INDEX) || (interPos!=ERROR_INDEX)) {
          if (classPos>interPos) {
            
            curPos += "class".length();
          }
          else {
            
            curPos += "interface".length();
          }
          int nameStart = getFirstNonWSCharPos(curPos);
          if (nameStart==ERROR_INDEX) { throw new ClassNameNotFoundException("Cannot determine enclosing class name"); }
          int nameEnd = nameStart+1;
          while(nameEnd<text.length()) {
            if ((!Character.isJavaIdentifierPart(text.charAt(nameEnd))) && (text.charAt(nameEnd)!='.')) {
              
              break;
            }
            ++nameEnd;
          }
          name = text.substring(nameStart,nameEnd) + '$' + name;
        }
        else if (newPos!=ERROR_INDEX) {
          name = String.valueOf(_getAnonymousInnerClassIndex(curPos)) + "$" + name;
          curPos = newPos;
        }
        else {
          
          break;
        }
      } while(qual);
    }
    finally { readUnlock(); }
    
    
    if (name.length()>0) name = name.substring(0, name.length()-1);
    
    if (qual) {
      String pn = getPackageName();
      if ((pn.length()>0) && (name.length()>0)) {
        name = getPackageName() + "." + name;
      }
    }

    return name;
  }
  
  
  private boolean _isAnonymousInnerClass(int newPos, int openSquigglyPos) throws BadLocationException {




    
    
    StringBuffer keyBuf = 
      new StringBuffer("_getAnonymousInnerClassIndex:").append(newPos).append(':').append(openSquigglyPos);
    String key = keyBuf.toString();
    Boolean cached = (Boolean) _checkCache(key);
    if (cached != null) {

      return cached;
    }

    
    cached = false;
    String text = getText(DOCSTART, openSquigglyPos+1);
    int origNewPos = newPos;
    newPos += "new".length();
    int classStart = getFirstNonWSCharPos(newPos);
    if (classStart!=ERROR_INDEX) { 
      int classEnd = classStart+1;
      while(classEnd<text.length()) {
        if ((!Character.isJavaIdentifierPart(text.charAt(classEnd))) && (text.charAt(classEnd)!='.')) {
          
          break;
        }
        ++classEnd;
      }
      
      int parenStart = getFirstNonWSCharPos(classEnd);
      if (parenStart!=ERROR_INDEX) {
        int origParenStart = parenStart;

        
        if (text.charAt(origParenStart)=='<') {
          parenStart = ERROR_INDEX;
          
          int closePointyBracket = findNextEnclosingBrace(origParenStart, '<', '>');
          if (closePointyBracket!=ERROR_INDEX) {
            if (text.charAt(closePointyBracket)=='>') {
              parenStart = getFirstNonWSCharPos(closePointyBracket+1);
            }
          }
        }
      }
      if (parenStart!=ERROR_INDEX) {
        if (text.charAt(parenStart)=='(') {
          final int origLocation = _currentLocation;
          _reduced.move(parenStart+1 - origLocation);  
          int parenEnd = balanceForward();
          _reduced.move(origLocation - (parenStart+1));    
          if (parenEnd > -1) {
            parenEnd = parenEnd + parenStart+1;
            
            int afterParen = getFirstNonWSCharPos(parenEnd);
            
            cached = (afterParen==openSquigglyPos);          
          }
        }
      }
    }
    
    _storeInCache(key, cached);
    

    return cached;
  }
  
  
  public String getPackageName() {
    try { return getStrictPackageName(); }
    catch(InvalidPackageException e) { return ""; }
  }
 
  
  int _getAnonymousInnerClassIndex(int pos) throws BadLocationException, ClassNameNotFoundException {   

    
    
    StringBuffer keyBuf = new StringBuffer("_getAnonymousInnerClassIndex:").append(pos);
    String key = keyBuf.toString();
    Integer cached = (Integer) _checkCache(key);
    if (cached != null) {

      return cached.intValue();
    }

    
    --pos; 
    char[] delims = {'{','}','(',')','[',']','+','-','/','*',';',':','=',
      '!','@','#','$','%','^','~','\\','"','`','|'};
    String className = getEnclosingClassName(pos, true);
    String text = getText(DOCSTART, pos);
    int index = 1;
    int newPos = pos;

    while((newPos = _findPrevKeyword(text, "new", newPos-1)) != ERROR_INDEX) {

      int afterNewPos = newPos + "new".length();
      int classStart = getFirstNonWSCharPos(afterNewPos);
      if (classStart==ERROR_INDEX) { continue; }
      int classEnd = classStart+1;
      while(classEnd<text.length()) {
        if ((!Character.isJavaIdentifierPart(text.charAt(classEnd))) && (text.charAt(classEnd)!='.')) {
          
          break;
        }
        ++classEnd;
      }

      int parenStart = getFirstNonWSCharPos(classEnd);
      if (parenStart==ERROR_INDEX) { continue; }
      int origParenStart = parenStart;
      

      if (text.charAt(origParenStart)=='<') {
        parenStart = ERROR_INDEX;
        
        int closePointyBracket = findNextEnclosingBrace(origParenStart, '<', '>');
        if (closePointyBracket!=ERROR_INDEX) {
          if (text.charAt(closePointyBracket)=='>') {
            parenStart = getFirstNonWSCharPos(closePointyBracket+1);
          }
        }
      }
      if (parenStart==ERROR_INDEX) { continue; }      
      if (text.charAt(parenStart)!='(') { continue; }
      int parenEnd = findNextEnclosingBrace(parenStart, '(', ')');
    
      int nextOpenSquiggly = _findNextOpenSquiggly(text, parenEnd);
      if (nextOpenSquiggly==ERROR_INDEX) { continue; }


      if (_isAnonymousInnerClass(newPos, nextOpenSquiggly)) {

        String cn = getEnclosingClassName(newPos, true);

        if (!cn.startsWith(className)) { break; }
        else if (!cn.equals(className)) {
          newPos = findPrevEnclosingBrace(newPos, '{', '}');
          continue;
        }
        else {
          ++index;
        }
      }
    }
    _storeInCache(key, new Integer(index));

    return index;
  }

  
  protected String getStrictPackageName() throws InvalidPackageException {
    

    
    StringBuffer buf = new StringBuffer();
    int oldLocation = 0;  
    
    readLock();
    try {
      final String text = getText();
      final int docLength = text.length();
      if (docLength == 0) return "";
      
      
      synchronized(_reduced) {
        oldLocation = _currentLocation;
        try {
          setCurrentLocation(0);
          
          
          int firstNormalLocation = 0;
          while (firstNormalLocation < docLength) {
            setCurrentLocation(firstNormalLocation);
            
            if (_reduced.currentToken().getHighlightState() == HighlightStatus.NORMAL) {
              
              char curChar = text.charAt(firstNormalLocation);
              if (! Character.isWhitespace(curChar)) break;
            }
            firstNormalLocation++;
          }
          
          
          
          
          if (firstNormalLocation == docLength) return "";
          
          final int strlen = "package".length();
          
          final int endLocation = firstNormalLocation + strlen;
          
          if ((firstNormalLocation + strlen > docLength) ||
              ! text.substring(firstNormalLocation, endLocation).equals("package")) {
            
            
            return "";
          }
          
          
          
          int afterPackage = firstNormalLocation + strlen;
          
          int semicolonLocation = afterPackage;
          do {
            semicolonLocation = text.indexOf(";", semicolonLocation + 1);
            if (semicolonLocation == -1)
              throw new InvalidPackageException(firstNormalLocation,
                                                "No semicolon found to terminate package statement!");
            setCurrentLocation(semicolonLocation);
          }
          while (_reduced.currentToken().getHighlightState() != HighlightStatus.NORMAL);
          
          
          for (int walk = afterPackage + 1; walk < semicolonLocation; walk++) {
            setCurrentLocation(walk);
            if (_reduced.currentToken().getHighlightState() == HighlightStatus.NORMAL) {
              char curChar = text.charAt(walk);
              if (! Character.isWhitespace(curChar)) buf.append(curChar);
            }
          }
          
          String toReturn = buf.toString();
          if (toReturn.equals(""))
            throw new InvalidPackageException(firstNormalLocation,
                                              "Package name was not specified after the package keyword!");
          return toReturn;
        }
        finally { 
          setCurrentLocation(0);  
          setCurrentLocation(oldLocation);
        }
      }
    }
    finally { readUnlock(); }
  }

  
  public String getEnclosingTopLevelClassName(int pos) throws ClassNameNotFoundException {
    readLock();
    synchronized(_reduced) {
      int oldLocation = _currentLocation;
      try {
        setCurrentLocation(pos);
        IndentInfo info = getIndentInformation();
        
        
        int topLevelBracePos = -1;
        String braceType = info.braceTypeCurrent;
        while (!braceType.equals(IndentInfo.noBrace)) {
          if (braceType.equals(IndentInfo.openSquiggly)) {
            topLevelBracePos = _currentLocation - info.distToBraceCurrent;
          }
          move(-info.distToBraceCurrent);
          info = getIndentInformation();
          braceType = info.braceTypeCurrent;
        }
        if (topLevelBracePos == -1) {
          
          setCurrentLocation(oldLocation);
          throw new ClassNameNotFoundException("no top level brace found");
        }
        
        char[] delims = {'{', '}', ';'};
        int prevDelimPos = findPrevDelimiter(topLevelBracePos, delims);
        if (prevDelimPos == ERROR_INDEX) {
          
          prevDelimPos = DOCSTART;
        }
        else prevDelimPos++;
        setCurrentLocation(oldLocation);
        
        
        return getNextTopLevelClassName(prevDelimPos, topLevelBracePos);
      }
      catch (BadLocationException ble) { throw new UnexpectedException(ble); }
      finally { 
        setCurrentLocation(oldLocation);
        readUnlock();
      }
    }
  }

  
  public String getFirstTopLevelClassName() throws ClassNameNotFoundException {
    return getNextTopLevelClassName(0, getLength());
  }

  
  public String getNextTopLevelClassName(int startPos, int endPos) throws ClassNameNotFoundException {

    readLock();
    synchronized(_reduced) {
      int oldLocation = _currentLocation;
      
      try {
        setCurrentLocation(startPos);
        final int textLength = endPos - startPos;
        final String text = getText(startPos, textLength);
        
        int index;
        
        int indexOfClass = _findKeywordAtToplevel("class", text, startPos);
        int indexOfInterface = _findKeywordAtToplevel("interface", text, startPos);
        int indexOfEnum = _findKeywordAtToplevel("enum",text,startPos);
        
        
        
        if (indexOfClass > -1 && (indexOfInterface <= -1 || indexOfClass < indexOfInterface) 
              && (indexOfEnum <= -1 || indexOfClass < indexOfEnum)) {
          index = indexOfClass + "class".length();
        }
        else if (indexOfInterface > -1 && (indexOfClass <= -1 || indexOfInterface < indexOfClass) 
                  && (indexOfEnum <= -1 || indexOfInterface < indexOfEnum)) {
          index = indexOfInterface + "interface".length();
        }
        else if (indexOfEnum > -1 && (indexOfClass <= -1 || indexOfEnum < indexOfClass)   
                   && (indexOfInterface <= -1 || indexOfEnum < indexOfInterface)) {
          index = indexOfEnum + "enum".length();
        }
        else {
          
          throw new ClassNameNotFoundException("No top level class name found");
        }
        
        
        
        index = getFirstNonWSCharPos(startPos + index) - startPos;
        if (index == -1) throw new ClassNameNotFoundException("No top level class name found");
        
        int endIndex = textLength; 
        
        
        char c;
        for (int i = index; i < textLength; i++) {
          c = text.charAt(i);
          if (!Character.isJavaIdentifierPart(c)) {
            endIndex = i;
            break;
          }
        }
        return text.substring(index,endIndex);
      }
      catch (BadLocationException ble) { throw new UnexpectedException(ble); }
      finally { 
        setCurrentLocation(oldLocation);
        readUnlock();
      }
    }
  }

  
  private int _findKeywordAtToplevel(String keyword, String text, int textOffset) {
    
    readLock();
    synchronized(_reduced) {
      int oldLocation = _currentLocation;
      int index = 0;
      try {
        while (true) {
          index = text.indexOf(keyword, index);
          if (index == -1) break; 
          else {
            
            setCurrentLocation(textOffset + index);
            
            
            ReducedToken rt = _reduced.currentToken();
            int indexPastKeyword = index + keyword.length();
            if (indexPastKeyword < text.length()) {
              if (rt.getState() == ReducedModelStates.FREE &&
                  Character.isWhitespace(text.charAt(indexPastKeyword))) {
                
                if (!posNotInBlock(index)) index = -1; 
                break;
              }
              else index++;  
            }
            else { 
              index = -1;
              break;
            }
          }
        }
        setCurrentLocation(oldLocation);
        return index;
      }
      finally { readUnlock(); }
    }
  }
  
  
  public static class WrappedPosition implements Position {
    private Position _wrapped;
    public WrappedPosition(Position w) { setWrapped(w); }
    public void setWrapped(Position w) { _wrapped = w; }
    public int getOffset() { return _wrapped.getOffset(); }
  }
  
  
  public synchronized Position createPosition(int offs) throws BadLocationException {
    WrappedPosition wp = new WrappedPosition(super.createPosition(offs));
    if (_wrappedPosList==null) { _wrappedPosList = new LinkedList<WeakReference<WrappedPosition>>(); }
    _wrappedPosList.add(new WeakReference<WrappedPosition>(wp));
    return wp;
  }
  
  
  public synchronized WeakHashMap<WrappedPosition, Integer> getWrappedPositionOffsets() {
    LinkedList<WeakReference<WrappedPosition>> newList = new LinkedList<WeakReference<WrappedPosition>>();
    if (_wrappedPosList==null) { _wrappedPosList = new LinkedList<WeakReference<WrappedPosition>>(); }
    WeakHashMap<WrappedPosition, Integer> ret = new WeakHashMap<WrappedPosition, Integer>(_wrappedPosList.size());
    
    for (WeakReference<WrappedPosition> wr: _wrappedPosList) {
      if (wr.get()!=null)  {
        
        newList.add(wr);
        ret.put(wr.get(), wr.get().getOffset());
      }
    }
    _wrappedPosList.clear();
    _wrappedPosList = newList;
    
    return ret;
  }
 
  
  public synchronized void setWrappedPositionOffsets(WeakHashMap<WrappedPosition, Integer> whm) throws BadLocationException {
    if (_wrappedPosList==null) { _wrappedPosList = new LinkedList<WeakReference<WrappedPosition>>(); }
    _wrappedPosList.clear();
    
    for(Map.Entry<WrappedPosition, Integer> entry: whm.entrySet()) {
      if (entry.getKey()!=null) {
        
        WrappedPosition wp = entry.getKey();
        wp.setWrapped(super.createPosition(entry.getValue()));
        _wrappedPosList.add(new WeakReference<WrappedPosition>(wp));
      }
    }
  }
  
  
  private static class CommandUndoableEdit extends AbstractUndoableEdit {
    private final Runnable _undoCommand;
    private final Runnable _redoCommand;

    public CommandUndoableEdit(final Runnable undoCommand, final Runnable redoCommand) {
      _undoCommand = undoCommand;
      _redoCommand = redoCommand;
    }

    public void undo() throws CannotUndoException {
      super.undo();
      _undoCommand.run();
    }

    public void redo() throws CannotRedoException {
      super.redo();
      _redoCommand.run();
    }

    public boolean isSignificant() { return false; }
  }

  
  public CompoundUndoManager getUndoManager() { return _undoManager; }

  
  public void resetUndoManager() {
    _undoManager = new CompoundUndoManager(_notifier);
    _undoManager.setLimit(UNDO_LIMIT);
  }

  
  public UndoableEdit getNextUndo() { return _undoManager.getNextUndo(); }

  
  public UndoableEdit getNextRedo() { return _undoManager.getNextRedo(); }

  
  public void documentSaved() { _undoManager.documentSaved(); }
  
  protected int startCompoundEdit() { return _undoManager.startCompoundEdit(); }
  
  protected void endCompoundEdit(int key) {
    _undoManager.endCompoundEdit(key);
  }
  
  
  protected void endLastCompoundEdit() { _undoManager.endLastCompoundEdit(); }
   
  protected void addUndoRedo(AbstractDocument.DefaultDocumentEvent chng, Runnable undoCommand, Runnable doCommand) {
    chng.addEdit(new CommandUndoableEdit(undoCommand, doCommand));    
  }
  
  
  
  
  
  


















  
  
  private List<FinalizationListener<DefinitionsDocument>> _finalizationListeners = 
    new LinkedList<FinalizationListener<DefinitionsDocument>>();
  
  
  public void addFinalizationListener(FinalizationListener<DefinitionsDocument> fl) {
    synchronized(_finalizationListeners) { _finalizationListeners.add(fl); }
  }
  
  public List<FinalizationListener<DefinitionsDocument>> getFinalizationListeners() {
    return _finalizationListeners;
  }

  
  protected void finalize() {
    FinalizationEvent<DefinitionsDocument> fe = new FinalizationEvent<DefinitionsDocument>(this);
    synchronized(_finalizationListeners) {
      for (FinalizationListener<DefinitionsDocument> fl: _finalizationListeners) {
        fl.finalized(fe);
      }
    }
  }
  
  public String toString() { return "ddoc for " + _odd; }
}
