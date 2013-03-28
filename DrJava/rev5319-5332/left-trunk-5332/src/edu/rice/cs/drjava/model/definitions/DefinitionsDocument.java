

package edu.rice.cs.drjava.model.definitions;

import java.awt.EventQueue;
import javax.swing.text.*;
import javax.swing.undo.*;
import javax.swing.event.DocumentEvent;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.WeakHashMap;
import java.lang.ref.WeakReference;

import java.io.Reader;
import java.io.StringReader;
import java.io.IOException;

import edu.rice.cs.drjava.model.definitions.reducedmodel.*;
import koala.dynamicjava.parser.impl.Parser;
import koala.dynamicjava.parser.impl.ParseException;
import koala.dynamicjava.parser.impl.TokenMgrError;
import edu.rice.cs.util.Log;
import edu.rice.cs.util.UnexpectedException;
import edu.rice.cs.util.swing.Utilities;
import edu.rice.cs.drjava.model.definitions.indent.Indenter;
import edu.rice.cs.drjava.model.OpenDefinitionsDocument;
import edu.rice.cs.drjava.model.*;

import static edu.rice.cs.drjava.model.definitions.reducedmodel.ReducedModelStates.*;


public class DefinitionsDocument extends AbstractDJDocument implements Finalizable<DefinitionsDocument> {
  
  public static final Log _log = new Log("GlobalModel.txt", false);
  private static final int NO_COMMENT_OFFSET = 0;
  private static final int WING_COMMENT_OFFSET = 2;
  
  private final List<DocumentClosedListener> _closedListeners = new LinkedList<DocumentClosedListener>();
  
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
      _closedListeners.clear();
    }
  }
  
  
  
  private static final int UNDO_LIMIT = 1000;
  
  private static boolean _tabsRemoved = true;
  
  
  private volatile boolean _isModifiedSinceSave = false;
  
  
  private volatile OpenDefinitionsDocument _odd;
  
  private volatile CompoundUndoManager _undoManager;
  
  
  private final GlobalEventNotifier _notifier;
  
  
  private final DefinitionsEditorKit _editor;
  
  
  
  
  private volatile LinkedList<WeakReference<WrappedPosition>> _wrappedPosList;
  
  
  public DefinitionsDocument(Indenter indenter, GlobalEventNotifier notifier) {
    super(indenter);
    _notifier = notifier;
    _editor = new DefinitionsEditorKit(notifier);
    resetUndoManager();
  }
  
  
  public DefinitionsDocument(GlobalEventNotifier notifier) {
    super();
    _notifier = notifier;
    _editor = new DefinitionsEditorKit(notifier);
    resetUndoManager();
  }
  
  
  public DefinitionsDocument(GlobalEventNotifier notifier, CompoundUndoManager undoManager) {
    super();
    _notifier = notifier;
    _editor = new DefinitionsEditorKit(notifier);
    _undoManager = undoManager;
  }
  
  
  public DefinitionsEditorKit getEditor(){
    return _editor;
  }
  
  
  protected Indenter makeNewIndenter(int indentLevel) { return new Indenter(indentLevel); }
  
  
  public void setOpenDefDoc(OpenDefinitionsDocument odd) { if (_odd == null) _odd = odd; }
  
  
  public OpenDefinitionsDocument getOpenDefDoc() {
    if (_odd == null)
      throw new IllegalStateException("The OpenDefinitionsDocument for this DefinitionsDocument has never been set");
    else return _odd;
  }
  
  
  protected void _styleChanged() {    
    
      int length = getLength() - _currentLocation;
      
      
      DocumentEvent evt = new DefaultDocumentEvent(_currentLocation, length, DocumentEvent.EventType.CHANGE);
      fireChangedUpdate(evt);
  } 
   





  
































  
  












  
  
  
  public String getQualifiedClassName() throws ClassNameNotFoundException {
    return getPackageQualifier() + getMainClassName();
  }
  
  
  public String getQualifiedClassName(int pos) throws ClassNameNotFoundException {
    return getPackageQualifier() + getEnclosingTopLevelClassName(pos);
  }
  
  
  private String getPackageQualifier() {
    String packageName = getPackageName();
    if ((packageName != null) && (! packageName.equals(""))) { packageName = packageName + "."; }
    return packageName;
  }
  
  
  public void insertString(int offset, String str, AttributeSet a) throws BadLocationException {
    if (_tabsRemoved) str = _removeTabs(str);
    _setModifiedSinceSave();
    super.insertString(offset, str, a);
  }
    
  
  public void remove(int offset, int len) throws BadLocationException {
    
    if (len == 0) return;
    _setModifiedSinceSave();
    super.remove(offset, len);
  }
  
  
  static String _removeTabs(final String source) { return source.replace('\t', ' '); }
  
  
  public void updateModifiedSinceSave() {
    _isModifiedSinceSave = _undoManager.isModified();
    if (_odd != null) _odd.documentReset();
  }
  
  
  private void _setModifiedSinceSave() {
 assert Utilities.TEST_MODE || EventQueue.isDispatchThread();
    if (! _isModifiedSinceSave) {
      _isModifiedSinceSave = true;
      if (_odd != null) _odd.documentModified();  
    }    
  }
  
  
  public void resetModification() {
    _isModifiedSinceSave = false;
    _undoManager.documentSaved();
    if (_odd != null) _odd.documentReset();  
  }
  
  
  public boolean isModifiedSinceSave() { return  _isModifiedSinceSave; }
  
  
  public int getCurrentCol() {
      Element root = getDefaultRootElement();
      int line = root.getElementIndex(_currentLocation);
      return _currentLocation - root.getElement(line).getStartOffset();
  }
  
  
  public int getCurrentLine() { return getLineOfOffset(_currentLocation); }
  
  
  public int getLineOfOffset(int offset) { return getDefaultRootElement().getElementIndex(offset) + 1; }
  
  
  public int _getOffset(int lineNum) {
    if (lineNum <= 0) return -1;
    if (lineNum == 1) return 0;
    

      final int origPos = getCurrentLocation();
      try {
        final int docLen = getLength();
        
        setCurrentLocation(0); 
        int i;
        for (i = 1; (i < lineNum) && (_currentLocation < docLen); i++) {
          int dist = _reduced.getDistToNextNewline();     
          if (_currentLocation + dist < docLen) dist++;  
          move(dist);  
        }
        if (i == lineNum) return _currentLocation;
        else return -1;
      }
      finally { setCurrentLocation(origPos); }

  }

  
  
  public boolean tabsRemoved() { return _tabsRemoved; }
  
  
  public int commentLines(int selStart, int selEnd) {
    
    
    int toReturn = selEnd;
    if (selStart == selEnd) {
      setCurrentLocation(_getLineStartPos(selStart));

      _commentLine();
      toReturn += WING_COMMENT_OFFSET;
    }
    else toReturn = commentBlock(selStart, selEnd);   
    _undoManager.endLastCompoundEdit();  
    return toReturn;
  }
  
  
  
  private int commentBlock(final int start, final int end) {
    int afterCommentEnd = end;
    try {
      
      
      final Position endPos = this.createUnwrappedPosition(end);
      
      int walker = _getLineStartPos(start);
      while (walker < endPos.getOffset()) {
        setCurrentLocation(walker);  
        
        _commentLine();              
        afterCommentEnd += WING_COMMENT_OFFSET;
        
        walker = walker + 2;         
        setCurrentLocation(walker);  
        
        
        walker += _reduced.getDistToNextNewline() + 1;
      }
    } 
    catch (BadLocationException e) { throw new UnexpectedException(e); }
    return afterCommentEnd;
  }
  
  
  private void _commentLine() {
    
    
    try { insertString(_currentLocation, "//", null); }
    catch (BadLocationException e) { throw new UnexpectedException(e); }
  }
  
  
  public int uncommentLines(int selStart, int selEnd) {
    
    
    int toReturn = selEnd;
    if (selStart == selEnd) {
      try {
        setCurrentLocation(_getLineStartPos(selStart));
        _uncommentLine();  
        toReturn -= WING_COMMENT_OFFSET;
      }
      catch (BadLocationException e) { throw new UnexpectedException(e); }
    }
    else  toReturn = uncommentBlock(selStart, selEnd);
    
    _undoManager.endLastCompoundEdit();
    return toReturn;
  }
  
  
  private int uncommentBlock(final int start, final int end) {
    int afterUncommentEnd = end;
    try {
      
      
      final Position endPos = this.createUnwrappedPosition(end);
      
      
      int walker = _getLineStartPos(start);

      while (walker < endPos.getOffset()) {
        setCurrentLocation(walker);           
        int diff = _uncommentLine();          
        afterUncommentEnd -= diff;            
        walker = _getLineEndPos(walker) + 1;   

      }           
    }
    catch (BadLocationException e) { throw new UnexpectedException(e); }
    return afterUncommentEnd;
  }
  
  
  private int _uncommentLine() throws BadLocationException {
    



    int pos1 = getText().indexOf("//", _currentLocation);  
    if (pos1 < 0) return NO_COMMENT_OFFSET;
    int pos2 = getFirstNonWSCharPos(_currentLocation, true);

    if (pos1 != pos2) return NO_COMMENT_OFFSET;
    
    remove(pos1, 2);
    return WING_COMMENT_OFFSET;
  }

  
  public void gotoLine(int line) {
    
    int dist;
    if (line < 0) return;
    int actualLine = 1;
    
    int len = getLength();
      setCurrentLocation(0);
      for (int i = 1; (i < line) && (_currentLocation < len); i++) {
        dist = _reduced.getDistToNextNewline();
        if (_currentLocation + dist < len) dist++;
        actualLine++;
        move(dist);  
      }
  }  
  
  
  private int _findNextOpenCurly(String text, int pos) throws BadLocationException {
    
 assert Utilities.TEST_MODE || EventQueue.isDispatchThread();
    int i;
    int reducedPos = pos;
    

      final int origLocation = _currentLocation;
      
      _reduced.move(pos - origLocation);  
      
      
      i = text.indexOf('{', reducedPos);
      while (i >- 1) {
        
        _reduced.move(i - reducedPos);  
        reducedPos = i;                 
        
        
        ReducedModelState state = _reduced.getStateAtCurrent();
        if (!state.equals(FREE) || _isStartOfComment(text, i)
              || ((i > 0) && _isStartOfComment(text, i - 1))) {
          i = text.indexOf('{', reducedPos+1);
          continue;  
        }
        else {
          break; 
        }        
      }  
      _reduced.move(origLocation - reducedPos);    

    
    if (i == -1) reducedPos = -1; 
    return reducedPos;  
  }
  
  
  public int _findPrevKeyword(String text, String kw, int pos) throws BadLocationException {
    
 assert Utilities.TEST_MODE || EventQueue.isDispatchThread();
    
    int i;
    int reducedPos = pos;
    

      final int origLocation = _currentLocation;
      
      _reduced.move(pos - origLocation);  
      
      
      i = text.lastIndexOf(kw, reducedPos);
      while (i >- 1) {
        
        if (i > 0) {
          if (Character.isJavaIdentifierPart(text.charAt(i-1))) {
            
            i = text.lastIndexOf(kw, i - 1);
            continue;  
          }
        }
        
        if (i + kw.length() < text.length()) {
          if (Character.isJavaIdentifierPart(text.charAt(i+kw.length()))) {
            
            i = text.lastIndexOf(kw, i-1);
            continue;  
          }
        }
        
        
        _reduced.move(i - reducedPos);  
        reducedPos = i;                 
        
        
        ReducedModelState state = _reduced.getStateAtCurrent();
        if (!state.equals(FREE) || _isStartOfComment(text, i) || ((i > 0) && _isStartOfComment(text, i - 1))) {
          i = text.lastIndexOf(kw, reducedPos-1);
          continue;  
        }
        else break; 
      }  
      
      _reduced.move(origLocation - reducedPos);    

    
    if (i == -1) reducedPos = -1; 
    return reducedPos;  
  }
  

  
  
  public String getEnclosingClassName(int pos, boolean qual) throws BadLocationException, ClassNameNotFoundException {
      return _getEnclosingClassName(pos, qual);
  }
  
  
  public String _getEnclosingClassName(final int pos, final boolean qual) throws BadLocationException, 
    ClassNameNotFoundException {    
    
 assert Utilities.TEST_MODE || EventQueue.isDispatchThread();
    
    
    final Query key = new Query.EnclosingClassName(pos, qual);
    final String cached = (String) _checkCache(key);
    if (cached != null) return cached;
    
    final char[] delims = {'{','}','(',')','[',']','+','-','/','*',';',':','=','!','@','#','$','%','^','~','\\','"','`','|'};
    String name = "";
    
    final String text = getText(0, pos);  
    
    int curPos = pos;
    
    do {

      


      
      curPos = findPrevEnclosingBrace(curPos, '{', '}');
      if (curPos == -1) { break; }
      int classPos = _findPrevKeyword(text, "class", curPos);
      int interPos = _findPrevKeyword(text, "interface", curPos);
      int otherPos = findPrevDelimiter(curPos, delims);
      int newPos = -1;
      
      int closeParenPos = _findPrevNonWSCharPos(curPos);
      if (closeParenPos != -1 && text.charAt(closeParenPos) == ')') {
        
        int openParenPos = findPrevEnclosingBrace(closeParenPos, '(', ')');
        if (openParenPos != -1 && text.charAt(openParenPos) == '(') {
          
          newPos = _findPrevKeyword(text, "new", openParenPos);

          if (! _isAnonymousInnerClass(newPos, curPos)) {
            
            newPos = -1;
          }
        }
      }

      while (classPos != -1 || interPos != -1 || newPos != -1) {
        if (newPos != -1) {
          classPos = -1;
          interPos = -1;
          break;
        }
        else if (otherPos > classPos && otherPos > interPos) {
          if (text.charAt(otherPos) != '{' || text.charAt(otherPos) != '}') ++otherPos;
          curPos = findPrevEnclosingBrace(otherPos, '{', '}');
          classPos = _findPrevKeyword(text, "class", curPos);
          interPos = _findPrevKeyword(text, "interface", curPos);
          otherPos = findPrevDelimiter(curPos, delims);
          newPos = -1;
          
          closeParenPos = _findPrevNonWSCharPos(curPos);
          if (closeParenPos != -1 && text.charAt(closeParenPos) == ')') {
            
            int openParenPos = findPrevEnclosingBrace(closeParenPos, '(', ')');
            if (openParenPos != -1 && text.charAt(openParenPos) == '(') {
              
              newPos = _findPrevKeyword(text, "new", openParenPos);

              if (! _isAnonymousInnerClass(newPos, curPos)) newPos = -1;
            }
          }
        }
        else {
          
          curPos = Math.max(classPos, Math.max(interPos, newPos));
          break;
        }
      }
      
      if (classPos != -1 || interPos != -1) {
        if (classPos > interPos) curPos += "class".length();  
        else curPos += "interface".length();                  
        
        int nameStart = getFirstNonWSCharPos(curPos);
        if (nameStart==-1) { throw new ClassNameNotFoundException("Cannot determine enclosing class name"); }
        int nameEnd = nameStart + 1;
        while (nameEnd < text.length()) {
          if (! Character.isJavaIdentifierPart(text.charAt(nameEnd)) && text.charAt(nameEnd) != '.') break;
          ++nameEnd;
        }
        name = text.substring(nameStart,nameEnd) + '$' + name;
      }
      else if (newPos != -1) {
        name = String.valueOf(_getAnonymousInnerClassIndex(curPos)) + "$" + name;
        curPos = newPos;
      }
      else break; 
    } while(qual);
    
    
    if (name.length() > 0) name = name.substring(0, name.length() - 1);
    
    if (qual) {
      String pn = getPackageName();
      if ((pn.length() > 0) && (name.length() > 0)) {
        name = getPackageName() + "." + name;
      }
    }

    _storeInCache(key, name, pos);
    return name;
  }
  
  

  public boolean _isAnonymousInnerClass(final int pos, final int openCurlyPos) throws BadLocationException {




    
    
    final Query key = new Query.AnonymousInnerClass(pos, openCurlyPos);
    Boolean cached = (Boolean) _checkCache(key);
    if (cached != null) {

      return cached;
    }
    int newPos = pos;

      cached = false;

      String text = getText(0, openCurlyPos + 1);  
      newPos += "new".length();
      int classStart = getFirstNonWSCharPos(newPos);
      if (classStart != -1) { 
        int classEnd = classStart + 1;
        while (classEnd < text.length()) {
          if (! Character.isJavaIdentifierPart(text.charAt(classEnd)) && text.charAt(classEnd) != '.') {
            
            break;
          }
          ++classEnd;
        }
        
        

        int parenStart = getFirstNonWSCharPos(classEnd);
        if (parenStart != -1) {
          int origParenStart = parenStart;
          

          if (text.charAt(origParenStart) == '<') {
            parenStart = -1;
            
            int closePointyBracket = findNextEnclosingBrace(origParenStart, '<', '>');
            if (closePointyBracket != -1) {
              if (text.charAt(closePointyBracket) == '>') {
                parenStart = getFirstNonWSCharPos(closePointyBracket+1);
              }
            }
          }
        }
        
        if (parenStart != -1) {
          if (text.charAt(parenStart) == '(') {
            setCurrentLocation(parenStart + 1);   
            int parenEnd = balanceForward();
            if (parenEnd > -1) {
              parenEnd = parenEnd + parenStart + 1;

              int afterParen = getFirstNonWSCharPos(parenEnd);

              cached = (afterParen == openCurlyPos); 
            }
          }
        }
      }
      _storeInCache(key, cached, openCurlyPos);

      return cached;

  }
  
  
  public String getPackageName() {
    
    Reader r;
    r = new StringReader(getText()); 
    try { return new Parser(r).packageDeclaration(Parser.DeclType.TOP).getName(); }
    catch (ParseException e) { return ""; }
    
    
    catch (TokenMgrError e) { return ""; }
    catch (Error e) {
      
      String msg = e.getMessage();
      if (msg != null && msg.startsWith("Invalid escape character")) {
        return "";
      }
      else { throw e; }
    }
    finally {
      try { r.close(); }
      catch (IOException e) {  }
    }
  }
  
  
  int _getAnonymousInnerClassIndex(final int pos) throws BadLocationException, ClassNameNotFoundException {   

    
 assert Utilities.TEST_MODE || EventQueue.isDispatchThread();
    
    
    final Query key = new Query.AnonymousInnerClassIndex(pos);
    final Integer cached = (Integer) _checkCache(key);
    if (cached != null) {

      return cached.intValue();
    }

    int newPos = pos; 



    final String className = _getEnclosingClassName(newPos - 2 , true);  
    final String text = getText(0, newPos - 2);  
    int index = 1;
    

    while ((newPos = _findPrevKeyword(text, "new", newPos - 4)) != -1) { 

      int afterNewPos = newPos + "new".length();
      int classStart = getFirstNonWSCharPos(afterNewPos);
      if (classStart == -1) { continue; }
      int classEnd = classStart + 1;
      while (classEnd < text.length()) {
        if (! Character.isJavaIdentifierPart(text.charAt(classEnd)) && text.charAt(classEnd) != '.') {
          
          break;
        }
        ++classEnd;
      }

      int parenStart = getFirstNonWSCharPos(classEnd);
      if (parenStart == -1) { continue; }
      int origParenStart = parenStart;
      

      if (text.charAt(origParenStart) == '<') {
        parenStart = -1;
        
        int closePointyBracket = findNextEnclosingBrace(origParenStart, '<', '>');
        if (closePointyBracket != -1) {
          if (text.charAt(closePointyBracket) == '>') {
            parenStart = getFirstNonWSCharPos(closePointyBracket + 1);
          }
        }
      }
      if (parenStart == -1) { continue; }      
      if (text.charAt(parenStart) != '(') { continue; }
      int parenEnd = findNextEnclosingBrace(parenStart, '(', ')');
      
      int nextOpenCurly = _findNextOpenCurly(text, parenEnd);
      if (nextOpenCurly == -1) { continue; }



      if (_isAnonymousInnerClass(newPos, nextOpenCurly)) {

        String cn = _getEnclosingClassName(newPos, true);

        if (! cn.startsWith(className)) { break; }
        else if (! cn.equals(className)) {
          newPos = findPrevEnclosingBrace(newPos, '{', '}');
          continue;
        }
        else ++index;
      }
    }
    _storeInCache(key, index, pos);

    return index;
  }
  
  
  public String getEnclosingTopLevelClassName(int pos) throws ClassNameNotFoundException {
      int oldPos = _currentLocation;
      try {
        setCurrentLocation(pos);
        BraceInfo info = _getEnclosingBrace();
        
        
        int topLevelBracePos = -1;
        String braceType = info.braceType();
        while (! braceType.equals(BraceInfo.NONE)) {
          if (braceType.equals(BraceInfo.OPEN_CURLY)) {
            topLevelBracePos = _currentLocation - info.distance();
          }
          move(-info.distance());
          info = _getEnclosingBrace();
          braceType = info.braceType();
        }
        if (topLevelBracePos == -1) {
          
          setCurrentLocation(oldPos);
          throw new ClassNameNotFoundException("no top level brace found");
        }
        
        char[] delims = {'{', '}', ';'};
        int prevDelimPos = findPrevDelimiter(topLevelBracePos, delims);
        if (prevDelimPos == -1) {
          
          prevDelimPos = 0;
        }
        else prevDelimPos++;
        setCurrentLocation(oldPos);
        
        
        return getNextTopLevelClassName(prevDelimPos, topLevelBracePos);
      }
      catch (BadLocationException ble) { throw new UnexpectedException(ble); }
      finally { setCurrentLocation(oldPos); }
  }
  
  
  private String getFirstClassName(int indexOfClass, int indexOfInterface,
                                   int indexOfEnum) throws ClassNameNotFoundException {
    try {
      if ((indexOfClass == -1) && (indexOfInterface == -1) && (indexOfEnum == -1)) throw ClassNameNotFoundException.DEFAULT;
      
      
      
      if ((indexOfEnum == -1) || 
          ((indexOfClass != -1) && (indexOfClass < indexOfEnum)) ||
          ((indexOfInterface != -1) && (indexOfInterface < indexOfEnum))) {
        
        
        
        if ((indexOfInterface == -1) ||
            ((indexOfClass != -1) && (indexOfClass < indexOfInterface))) {
          
          return getNextIdentifier(indexOfClass + "class".length());
        }
        else {
          
          return getNextIdentifier(indexOfInterface + "interface".length());
        }
      }
      else {
        
        return getNextIdentifier(indexOfEnum + "enum".length());
      }    
    }
    catch(IllegalStateException ise) { throw ClassNameNotFoundException.DEFAULT; }
  }
  
  
  public String getMainClassName() throws ClassNameNotFoundException {
      final int oldPos = _currentLocation;
      
      try {
        setCurrentLocation(0);
        final String text = getText();  
        
        final int indexOfClass = _findKeywordAtToplevel("class", text, 0);
        final int indexOfInterface = _findKeywordAtToplevel("interface", text, 0);
        final int indexOfEnum = _findKeywordAtToplevel("enum", text, 0);
        final int indexOfPublic = _findKeywordAtToplevel("public", text, 0);
        
        if (indexOfPublic == -1)  return getFirstClassName(indexOfClass, indexOfInterface, indexOfEnum);
        


        
        
        final int afterPublic = indexOfPublic + "public".length();
        final String subText = text.substring(afterPublic);
        setCurrentLocation(afterPublic);

        int indexOfPublicClass  = _findKeywordAtToplevel("class", subText, afterPublic);  
        if (indexOfPublicClass != -1) indexOfPublicClass += afterPublic;
        int indexOfPublicInterface = _findKeywordAtToplevel("interface", subText, afterPublic); 
        if (indexOfPublicInterface != -1) indexOfPublicInterface += afterPublic;
        int indexOfPublicEnum = _findKeywordAtToplevel("enum", subText, afterPublic); 
        if (indexOfPublicEnum != -1) indexOfPublicEnum += afterPublic;

        
        return getFirstClassName(indexOfPublicClass, indexOfPublicInterface, indexOfPublicEnum);
        
      }
      finally { setCurrentLocation(oldPos); }
  }
  
  
  public String getFirstTopLevelClassName() throws ClassNameNotFoundException {
    return getNextTopLevelClassName(0, getLength());
  }
  
  
  public String getNextTopLevelClassName(int startPos, int endPos) throws ClassNameNotFoundException {
      int oldPos = _currentLocation;
      
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
          
          throw ClassNameNotFoundException.DEFAULT;
        }
        
        
        return getNextIdentifier(startPos + index);
      }
      catch (BadLocationException ble) { throw new UnexpectedException(ble); }
      catch (IllegalStateException e) { throw new ClassNameNotFoundException("No top level class name found"); }
      finally { setCurrentLocation(oldPos); }
  }
  
  
  private String getNextIdentifier(final int startPos) throws ClassNameNotFoundException {
    

    





    try {
      
      int index = getFirstNonWSCharPos(startPos);
      if (index == -1) throw new IllegalStateException("No identifier found");
      
      String text = getText();
      int length = text.length(); 
      int endIndex = length; 
      


      
      
      char c;
      for (int i = index; i < length; i++) {
        c = text.charAt(i);
        if (! Character.isJavaIdentifierPart(c)) {
          endIndex = i;
          break;
        }
      }

      return text.substring(index, endIndex);
    }
    catch(BadLocationException e) { 



      throw new UnexpectedException(e); 
    }
  }
  
  
  private int _findKeywordAtToplevel(String keyword, String text, int textOffset) {
    
    int oldPos = _currentLocation;
    int index = 0;
    while (true) {
      index = text.indexOf(keyword, index);
      if (index == -1) break; 
      else {
        
        setCurrentLocation(textOffset + index);
        
        
        int indexPastKeyword = index + keyword.length();
        if (indexPastKeyword < text.length()) {
          if (! isShadowed() && Character.isWhitespace(text.charAt(indexPastKeyword))) {
            
            if (! notInBlock(index)) index = -1; 
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
    setCurrentLocation(oldPos);

    return index;
  }
  
  
  public static class WrappedPosition implements Position {
    private Position _wrapped;
    
    WrappedPosition(Position w) { setWrapped(w); }
    public void setWrapped(Position w) { _wrapped = w; }
    public int getOffset() { return _wrapped.getOffset(); }
  }
  
  
  public Position createPosition(final int offset) throws BadLocationException {
    






    WrappedPosition wp = new WrappedPosition(createUnwrappedPosition(offset));
    synchronized(_wrappedPosListLock) {
      if (_wrappedPosList == null) _wrappedPosList = new LinkedList<WeakReference<WrappedPosition>>(); 
      _wrappedPosList.add(new WeakReference<WrappedPosition>(wp));
    }
    return wp;
  }
  
  
  public WeakHashMap<WrappedPosition, Integer> getWrappedPositionOffsets() {
    LinkedList<WeakReference<WrappedPosition>> newList = new LinkedList<WeakReference<WrappedPosition>>();
    synchronized(_wrappedPosListLock) {
      if (_wrappedPosList == null) { _wrappedPosList = new LinkedList<WeakReference<WrappedPosition>>(); }
      WeakHashMap<WrappedPosition, Integer> ret = new WeakHashMap<WrappedPosition, Integer>(_wrappedPosList.size());
      
      for (WeakReference<WrappedPosition> wr: _wrappedPosList) {
        if (wr.get() != null)  {
          
          newList.add(wr);
          ret.put(wr.get(), wr.get().getOffset());
        }
      }
      _wrappedPosList.clear();
      _wrappedPosList = newList;  
      return ret;
    }
  }
  
  
  public void setWrappedPositionOffsets(WeakHashMap<WrappedPosition, Integer> whm) throws BadLocationException {
    synchronized(_wrappedPosListLock) {
      if (_wrappedPosList == null) { _wrappedPosList = new LinkedList<WeakReference<WrappedPosition>>(); }
      _wrappedPosList.clear();
      
      for(Map.Entry<WrappedPosition, Integer> entry: whm.entrySet()) {
        if (entry.getKey() != null) {
          
          WrappedPosition wp = entry.getKey();
          wp.setWrapped(createUnwrappedPosition(entry.getValue()));
          _wrappedPosList.add(new WeakReference<WrappedPosition>(wp));
        }
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
  
  protected void endCompoundEdit(int key) { _undoManager.endCompoundEdit(key); }
  
  
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
  
  
  public boolean containsClassOrInterfaceOrEnum() throws BadLocationException {
    
 assert Utilities.TEST_MODE || EventQueue.isDispatchThread();
    int i, j;
    int reducedPos = 0;
    
    final String text = getText();
    final int origLocation = _currentLocation;
    try {
      
      _reduced.move(-origLocation);
      
      
      i = text.indexOf("class", reducedPos);
      j = text.indexOf("interface", reducedPos);
      if (i==-1) i = j; else if (j >= 0) i = Math.min(i,j);
      j = text.indexOf("enum", reducedPos);
      if (i==-1) i = j; else if (j >= 0) i = Math.min(i,j);
      while (i > - 1) {
        
        _reduced.move(i - reducedPos);  
        reducedPos = i;                 
        
        
        ReducedModelState state = _reduced.getStateAtCurrent();
        if (!state.equals(FREE) || _isStartOfComment(text, i) || ((i > 0) && _isStartOfComment(text, i - 1))) {
          i = text.indexOf("class", reducedPos+1);
          j = text.indexOf("interface", reducedPos+1);
          if (i==-1) i = j; else if (j >= 0) i = Math.min(i,j);
          j = text.indexOf("enum", reducedPos+1);
          if (i==-1) i = j; else if (j >= 0) i = Math.min(i,j);
          continue;  
        }
        else {
          return true; 
        }        
      }  
      
      return false;
    }
    finally {
      _reduced.move(origLocation - reducedPos);    
    }
  }
}
