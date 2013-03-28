

package edu.rice.cs.drjava.model;   

import edu.rice.cs.drjava.model.definitions.reducedmodel.ReducedModelStates;
  
import edu.rice.cs.util.UnexpectedException;
import edu.rice.cs.util.swing.DocumentIterator;
import edu.rice.cs.util.swing.Utilities;
import edu.rice.cs.util.text.AbstractDocumentInterface;

import javax.swing.text.BadLocationException;
import javax.swing.text.Position;


public class FindReplaceMachine {
  
  
  
  
  private OpenDefinitionsDocument _doc;      
  private OpenDefinitionsDocument _firstDoc; 
  private Position _current;                 

  private String _findWord;                  
  private String _replaceWord;               
  private boolean _matchCase;
  private boolean _matchWholeWord;
  private boolean _searchAllDocuments;       
  private boolean _isForward;                
  private boolean _ignoreCommentsAndStrings; 
  private String _lastFindWord;              
  private boolean _skipText;                 
  private DocumentIterator _docIterator;     
  private SingleDisplayModel _model;

  
  public FindReplaceMachine(SingleDisplayModel model, DocumentIterator docIterator) {    
    _skipText = false;


    _model = model;
    _docIterator = docIterator;
    setFindAnyOccurrence();
    setFindWord("");
    setReplaceWord("");
    setSearchBackwards(false);
    setMatchCase(true);
    setSearchAllDocuments(false);
    setIgnoreCommentsAndStrings(false);
  }
  
  public void cleanUp() {
    _docIterator = null;
    setFindWord("");
    _doc = null;
  }
  
  
  public void positionChanged() {
    _lastFindWord = null;
    _skipText = false;
  }

  public void setLastFindWord() { _lastFindWord = _findWord; }

  public boolean getSearchBackwards() { return ! _isForward; }

  public void setSearchBackwards(boolean searchBackwards) {
    if (_isForward == searchBackwards) {
      
      
      if (onMatch() && _findWord.equals(_lastFindWord)) _skipText = true;
      else _skipText = false;
    }
    _isForward = ! searchBackwards;
  }

  public void setMatchCase(boolean matchCase) { _matchCase = matchCase; }
  
  public void setMatchWholeWord() { _matchWholeWord = true; }
  
  public void setFindAnyOccurrence() { _matchWholeWord = false; }  

  public void setSearchAllDocuments(boolean searchAllDocuments) { _searchAllDocuments = searchAllDocuments; }
  
  public void setIgnoreCommentsAndStrings(boolean ignoreCommentsAndStrings) {
    _ignoreCommentsAndStrings = ignoreCommentsAndStrings;
  }

  public void setDocument(OpenDefinitionsDocument doc) { _doc = doc; }
  
  public void setFirstDoc(OpenDefinitionsDocument firstDoc) { _firstDoc = firstDoc; }
 
  public void setPosition(int pos) {

    assert (pos >= 0) && (pos <= _doc.getLength());
    try { _current = _doc.createPosition(pos); }
    catch (BadLocationException ble) { throw new UnexpectedException(ble); }
  }

  
  public int getCurrentOffset() { return _current.getOffset(); }

  public String getFindWord() { return _findWord; }

  public String getReplaceWord() { return _replaceWord; }

  public boolean getSearchAllDocuments() { return _searchAllDocuments; }

  public OpenDefinitionsDocument getDocument() { return _doc; }
  
  public OpenDefinitionsDocument getFirstDoc() { return _firstDoc; }

  
  public void setFindWord(String word) { _findWord = word; }

  
  public void setReplaceWord(String word) { _replaceWord = word; }

  
  public boolean onMatch() {
    String findWord = _findWord;
    int wordLen, off;
    
    if(_current == null) return false;
    
    wordLen = findWord.length();
    if (_isForward) off = getCurrentOffset() - wordLen;
    else off = getCurrentOffset();

    if (off < 0) return false;
    
     String matchSpace;
    _doc.readLock();
    try {
      if (off + wordLen > _doc.getLength()) return false;
      matchSpace = _doc.getText(off, wordLen);
    }
    catch (BadLocationException e) { throw new UnexpectedException(e); }
    finally { _doc.readUnlock(); }
    
    if (!_matchCase) {
      matchSpace = matchSpace.toLowerCase();
      findWord = findWord.toLowerCase();
    }
    return matchSpace.equals(findWord);
  }
  
  
  
  public boolean replaceCurrent() {

    if (! onMatch()) return false;
    _doc.modifyLock();
    try {

      int offset = getCurrentOffset();
      if (_isForward) offset -= _findWord.length();  

      

      
      _doc.remove(offset, _findWord.length());


      _doc.insertString(getCurrentOffset(), _replaceWord, null);
      
      
      if (_isForward) setPosition(offset + _replaceWord.length());
      else setPosition(offset);
      
      return true;
    }
    catch (BadLocationException e) { throw new UnexpectedException(e); }
    finally { _doc.modifyUnlock(); }
  }

  
  public int replaceAll() { return replaceAll(_searchAllDocuments); }
  
  
  private int replaceAll(boolean searchAll) {
    if (searchAll) {
      OpenDefinitionsDocument startDoc = _doc;
      int count = 0;           
      int n = _docIterator.getDocumentCount();
      for (int i = 0; i < n; i++) {
        
        count += _replaceAllInCurrentDoc();
        _doc = _docIterator.getNextDocument(_doc);
      }
      
      
      _model.getDocumentNavigator().repaint();
      
      return count;
    }
    else return _replaceAllInCurrentDoc();
  }
  
  
  private int _replaceAllInCurrentDoc() {

    _doc.modifyLock();
    try {
      if (_isForward) setPosition(0);
      else setPosition(_doc.getLength());
      
      int count = 0;
      FindResult fr = findNext(false);  

      
      while (! fr.getWrapped()) {
        replaceCurrent();
        count++;

        fr = findNext(false);           

      }
      return count;
    }
    finally { _doc.modifyUnlock(); }
  }
  
  public FindResult findNext() { return findNext(_searchAllDocuments); }

  
  private FindResult findNext(boolean searchAll) {
    
    
    _doc.readLock();
    FindResult fr;
    int start;
    int len;
    try {
      
      
      if (_skipText) {  

        int wordLen = _lastFindWord.length();
        if (_isForward) setPosition(getCurrentOffset() + wordLen);
        else setPosition(getCurrentOffset() - wordLen);
        positionChanged();
      }
      

      
      int offset = getCurrentOffset();


      if (_isForward) { 
        start = offset; 
        len = _doc.getLength() - offset; 
      }
      else { 
        start = 0; 
        len = offset; 
      }
      fr = _findNextInDoc(_doc, start, len, searchAll);
    }
    finally { _doc.readUnlock(); }
    if ((fr.getFoundOffset() >= 0) || ! searchAll) return fr;  
    
    
    return _findNextInOtherDocs(_doc, start, len);
  }

 
  
  private FindResult _findNextInDoc(OpenDefinitionsDocument doc, int start, int len, boolean searchAll) {
    

    FindResult fr = _findNextInDocSegment(doc, start, len);
    if (fr.getFoundOffset() >= 0 || searchAll) return fr;
    
    return _findWrapped(doc, start, len, false);  
  }
  
    
  private FindResult _findWrapped(OpenDefinitionsDocument doc, int start, int len, boolean allWrapped) {
    int newLen, newStart;
    
    assert (_isForward && start + len == doc.getLength()) || (! _isForward && start == 0);
    



    if (doc.getLength() == 0) return new FindResult(doc, -1, true, allWrapped);
    if (_isForward) {
      newLen = start;
      newStart = 0;
    }
    else {
      newStart = len;
      newLen = doc.getLength() - len;
    }


    return _findNextInDocSegment(doc, newStart, newLen, true, allWrapped);
  } 
      
      
    
  private FindResult _findNextInDocSegment(OpenDefinitionsDocument doc, int start, int len) {
    return _findNextInDocSegment(doc, start, len, false, false);
  }
  
  
  private FindResult 
    _findNextInDocSegment(OpenDefinitionsDocument doc, int start, int len, boolean wrapped, boolean allWrapped) {  

    
    if (len == 0 || doc.getLength() == 0) return new FindResult(doc, -1, wrapped, allWrapped);
    
    int docLen;     
    String text;    
    
    String findWord = _findWord;       
    int wordLen = findWord.length();   
    
    try { 
      docLen = doc.getLength();

      text = doc.getText(start, len);
      
      if (! _matchCase) {
        text = text.toLowerCase();
        findWord = findWord.toLowerCase();  
      }
      

      
      
      
      
      
      
      
      while (len >= wordLen) {
        
        
        int foundOffset = _isForward ? text.indexOf(findWord) : text.lastIndexOf(findWord);
        if (foundOffset < 0) break;  
        
        int foundLocation = start + foundOffset;
        int matchLocation;
        
        if (_isForward) {
          int adjustedOffset = foundOffset + wordLen;
          start += adjustedOffset;                       
          text = text.substring(adjustedOffset, len);    
          len = len - adjustedOffset;                    
          matchLocation = start;                         

        }
        else {
          len = foundOffset;                             
          matchLocation = start + foundOffset;           
          text = text.substring(0, len);                 

        }
        
        doc.setCurrentLocation(foundLocation);           
        

        if (_shouldIgnore(foundLocation, doc)) continue;
        
        _current = doc.createPosition(matchLocation);   
        

        
        return new FindResult(doc, matchLocation, wrapped, allWrapped);  
      }
    }
    catch (BadLocationException e) { throw new UnexpectedException(e); }
    
    
    return new FindResult(doc, -1, wrapped, allWrapped);
  }
  
  
  private FindResult _findNextInOtherDocs(final OpenDefinitionsDocument startDoc, int start, int len) {
    

  
    boolean allWrapped = false;
    _doc = _isForward ? _docIterator.getNextDocument(startDoc) : _docIterator.getPrevDocument(startDoc);

    while (_doc != startDoc) {
      if (_doc == _firstDoc) allWrapped = true;
      

      


      

      
      _doc.readLock();
      FindResult fr;
      try { fr = _findNextInDocSegment(_doc, 0, _doc.getLength(), false, allWrapped); } 
      finally { _doc.readUnlock(); }
      
      if (fr.getFoundOffset() >= 0) return fr;
      

      _doc = _isForward ? _docIterator.getNextDocument(_doc) : _docIterator.getPrevDocument(_doc);     

    }
    
    
    startDoc.readLock();
    try { return _findWrapped(startDoc, start, len, true); }  
    finally { startDoc.readUnlock(); } 
  } 
  
  
  private boolean wholeWordFoundAtCurrent(OpenDefinitionsDocument doc, int foundOffset) {    
    String docText;
    doc.readLock();
    try { docText = doc.getText(); }
    finally {doc.readUnlock();}      
    
    Character leftOfMatch = null;
    Character rightOfMatch = null;
    int leftLocation = foundOffset - 1;
    int rightLocation = foundOffset + _findWord.length();
    boolean leftOutOfBounds = false;
    boolean rightOutOfBounds = false;
    
    try { leftOfMatch = new Character(docText.charAt(leftLocation)); }
    catch (IndexOutOfBoundsException e) { leftOutOfBounds = true; }
    
    try { rightOfMatch = new Character(docText.charAt(rightLocation)); }
    catch (IndexOutOfBoundsException e) { rightOutOfBounds = true; }
    
    if (!leftOutOfBounds && !rightOutOfBounds) 
      return isDelimiter(rightOfMatch) && isDelimiter(leftOfMatch);
    if (!leftOutOfBounds) return isDelimiter(leftOfMatch);
    if (!rightOutOfBounds) return isDelimiter(rightOfMatch);
    return true;
  }

  
  private boolean isDelimiter(Character ch) {
    return !Character.isLetterOrDigit(ch.charValue());
  }
  
  
  private boolean _shouldIgnore(int foundOffset, OpenDefinitionsDocument odd) {
    return (_matchWholeWord && ! wholeWordFoundAtCurrent(odd, foundOffset)) || 
      (_ignoreCommentsAndStrings && odd.getStateAtCurrent() != ReducedModelStates.FREE);
  }
}