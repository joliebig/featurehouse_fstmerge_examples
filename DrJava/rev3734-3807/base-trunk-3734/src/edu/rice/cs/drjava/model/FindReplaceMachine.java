

package edu.rice.cs.drjava.model;   

import edu.rice.cs.drjava.model.definitions.reducedmodel.ReducedModelStates;
  
import edu.rice.cs.util.UnexpectedException;
import edu.rice.cs.util.swing.DocumentIterator;
import edu.rice.cs.util.text.AbstractDocumentInterface;

import javax.swing.text.BadLocationException;
import javax.swing.text.Position;


public class FindReplaceMachine {
  
  private AbstractDocumentInterface _doc;
  
  private AbstractDocumentInterface _firstDoc;
  
  private Position _start;
  
  private Position _current;
  
  private String _findWord;
  
  private String _replaceWord;

  private boolean _wrapped;
  private boolean _allDocsWrapped;
  private boolean _checkAllDocsWrapped;
  private boolean _matchCase;
  private boolean _matchWholeWord;
  private boolean _searchBackwards;
  private boolean _searchAllDocuments;
  private boolean _ignoreCommentsAndStrings;
  
  
  
  private String _lastFindWord;
  
  
  private boolean _skipOneFind;
  
  private DocumentIterator _docIterator;
  
  private SingleDisplayModel _model;
  
  
  public FindReplaceMachine(SingleDisplayModel model, DocumentIterator docIterator) {    
    _skipOneFind = false;
    _checkAllDocsWrapped = false;
    _allDocsWrapped = false;
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
    _skipOneFind = false;
  }

  public void setLastFindWord() { _lastFindWord = _findWord; }

  public boolean getSearchBackwards() { return _searchBackwards; }

  public void setSearchBackwards(boolean searchBackwards) {
    if (_searchBackwards != searchBackwards) {
      
      
      if (isOnMatch() && _findWord.equals(_lastFindWord)) _skipOneFind = true;
      else _skipOneFind = false;
    }
    _searchBackwards = searchBackwards;
  }

  public void setMatchCase(boolean matchCase) { _matchCase = matchCase; }
  
  public void setMatchWholeWord() { _matchWholeWord = true; }
  
  public void setFindAnyOccurrence() { _matchWholeWord = false; }  

  public void setSearchAllDocuments(boolean searchAllDocuments) { _searchAllDocuments = searchAllDocuments; }
  
  public void setIgnoreCommentsAndStrings(boolean ignoreCommentsAndStrings) {
    _ignoreCommentsAndStrings = ignoreCommentsAndStrings;
  }

  public void setDocument(AbstractDocumentInterface doc) { _doc = doc; }
  
  public void setFirstDoc(AbstractDocumentInterface firstDoc) { _firstDoc = firstDoc; }
 
  public void setPosition(int pos) {
    try { _current = _doc.createPosition(pos); }
    catch (BadLocationException ble) { throw new UnexpectedException(ble); }
  }

  public void setStart(int pos) {
    try {
      _start = _doc.createPosition(pos);

      _wrapped = false;
    }
    catch (BadLocationException ble) {
      throw new UnexpectedException(ble);
    }
  }

  
  public int getStartOffset() { return _start.getOffset(); }

  
  public int getCurrentOffset() { return _current.getOffset(); }

  public void makeCurrentOffsetStart() {
    try { _start = _doc.createPosition(getCurrentOffset()); }
    catch (BadLocationException e) { throw new UnexpectedException(e); }
  }

  public String getFindWord() { return _findWord; }

  public String getReplaceWord() { return _replaceWord; }

  public boolean getSearchAllDocuments() { return _searchAllDocuments; }

  public AbstractDocumentInterface getDocument() { return _doc; }
  
  public AbstractDocumentInterface getFirstDoc() { return _firstDoc; }

  
  public void setFindWord(String word) { _findWord = word; }

  
  public void setReplaceWord(String word) { _replaceWord = word; }

  
  public boolean isOnMatch() {
    String findWord = _findWord;
    int len, off;
    
    if(_current == null) return false;
    
    len = findWord.length();
    if (!_searchBackwards) off = _current.getOffset() - len;
    else off = _current.getOffset();

    if (off < 0) return false;
    
     String matchSpace;
    _doc.acquireReadLock();
    try {
      if (off + len > _doc.getLength()) return false;
      matchSpace = _doc.getText(off, len);
    }
    catch (BadLocationException e) { throw new UnexpectedException(e); }
    finally { _doc.releaseReadLock(); }
    
    if (!_matchCase) {
      matchSpace = matchSpace.toLowerCase();
      findWord = findWord.toLowerCase();
    }
    return matchSpace.equals(findWord);
  }
  
  
  
  public boolean replaceCurrent() {

    if (! isOnMatch()) return false;
    _doc.acquireWriteLock();
    try {
      boolean atStart = false;
      int position = getCurrentOffset();
      if (!_searchBackwards) position -= _findWord.length();
      _doc.remove(position, _findWord.length());
      if (position == 0) atStart = true;
      _doc.insertString(getCurrentOffset(), _replaceWord, null);
      
      
      
      
      
      if (atStart && !_searchBackwards) setPosition(_replaceWord.length());
      else if (! atStart && _searchBackwards) setPosition(getCurrentOffset() - _replaceWord.length());
      
      return true;
    }
    catch (BadLocationException e) { throw new UnexpectedException(e); }
    finally { _doc.releaseWriteLock(); }
  }

  
  public int replaceAll() {
    if (_searchAllDocuments) {
      AbstractDocumentInterface startDoc = _doc;
      _searchAllDocuments = false;
      
      int count = _replaceAllInCurrentDoc();
      _doc = _docIterator.getNextDocument(_doc);
      int n = _docIterator.getDocumentCount();
      for (int i=1; i < n; i++) {
        
        count += _replaceAllInCurrentDoc();
        _doc = _docIterator.getNextDocument(_doc);
      }
      _searchAllDocuments = true;
      return count;
    }
    else return _replaceAllInCurrentDoc();
  }
  
  
  private int _replaceAllInCurrentDoc() {
    _doc.acquireReadLock();
    try {
      if (!_searchBackwards) {
        _start = _doc.createPosition(0);
        setPosition(0);
      }
      else {
        _start = _doc.createPosition(_doc.getLength());
        setPosition(_doc.getLength());
      }
    }
    catch (BadLocationException e) { throw new UnexpectedException(e); }
    finally { _doc.releaseReadLock(); }
    int count = 0;
    FindResult fr = findNext();
    _doc = fr.getDocument();

    while (!fr.getWrapped()) {
      replaceCurrent();
      count++;
      fr = findNext();
      _doc = fr.getDocument();
    }
    return count;
  }

  
  public FindResult findNext() {    
    
    
    if (_skipOneFind) {
      int wordLength = _lastFindWord.length();
      if (!_searchBackwards) setPosition(getCurrentOffset() + wordLength);
      else setPosition(getCurrentOffset() - wordLength);
      positionChanged();
    }
    if (!_searchBackwards) return _findNext(_current.getOffset(), _doc.getLength()-_current.getOffset());
    return _findNext(0, _current.getOffset());
  }        
  
  
  private FindResult _findNext(int start, int end) {
    try {

      FindResult tempFr = new FindResult(_doc, -1, false, false);      
      int docLen;
      String findWord = _findWord;
      
      String findSpace;
      _doc.acquireReadLock();
      try {
        docLen = _doc.getLength();
        findSpace = _doc.getText(start, end);
      }
      finally { _doc.releaseReadLock(); }
      
      if (!_matchCase) {
        findSpace = findSpace.toLowerCase();
        findWord = findWord.toLowerCase();
      }
      
      
      int foundOffset;
      foundOffset = !_searchBackwards ? findSpace.indexOf(findWord) : findSpace.lastIndexOf(findWord);
          
      if (foundOffset >= 0) {
        int locationToIgnore = foundOffset + start;
        _model.getODDForDocument(_doc).setCurrentLocation(locationToIgnore);
        if (_shouldIgnore(locationToIgnore, _doc)) {
          foundOffset += start;
          if (!_searchBackwards) {
            foundOffset += findWord.length();
            return _findNext(foundOffset, docLen-foundOffset);
          }
          return _findNext(start, foundOffset); 
        }       
        

        foundOffset += start;
        if (!_searchBackwards) foundOffset += findWord.length();
        _current = _doc.createPosition(foundOffset);  
      }
      else { 
        if (_searchAllDocuments) {
          AbstractDocumentInterface nextDocToSearch;
          
          nextDocToSearch = 
            (!_searchBackwards ? _docIterator.getNextDocument(_doc) : _docIterator.getPrevDocument(_doc));
          
          tempFr = _findNextInAllDocs(nextDocToSearch, 0, nextDocToSearch.getLength());
          foundOffset = tempFr.getFoundOffset();
        }
        else { 
          _checkAllDocsWrapped = false;
          _allDocsWrapped = false;
        }
        
        if (foundOffset == -1) {   
          if (!_searchBackwards) foundOffset = _findWrapped(0, _current.getOffset() + (_findWord.length() - 1));
          else {
            int startBackOffset = _current.getOffset() - (_findWord.length() - 1);
            foundOffset = _findWrapped(startBackOffset, docLen - startBackOffset);
          }
        }
      }
      
      if (_checkAllDocsWrapped && tempFr.getDocument() == _firstDoc) {
        _allDocsWrapped = true;
        _checkAllDocsWrapped = false;
      }
      
      FindResult fr = new FindResult(tempFr.getDocument(), foundOffset, _wrapped, _allDocsWrapped);
      _wrapped = false;
      if (_allDocsWrapped = true) _allDocsWrapped = false;
      return fr;
    }
    catch (BadLocationException e) { throw new UnexpectedException(e); }
  }
  
  
  private int _findWrapped(int start, int end) {
    try{
      _wrapped = true;
      int docLen;
      String findSpace;
      
      _doc.acquireReadLock();  
      try { 
        docLen = _doc.getLength(); 
        if (!_searchBackwards) {
          if (end > docLen) end = docLen;
        }
              
        else {  
          if (start < 0){ 
            start = 0;
            end = docLen;
          }
        }
        findSpace = _doc.getText(start, end);
      }
      finally { _doc.releaseReadLock(); }
      
      String findWord = _findWord;
      
      if (!_matchCase) {
        findSpace = findSpace.toLowerCase();
        findWord = findWord.toLowerCase();
      }
      
      int foundOffset;
      foundOffset = !_searchBackwards ? findSpace.indexOf(findWord)
        : findSpace.lastIndexOf(findWord);
      
      if (foundOffset >= 0) {
        int locationToIgnore = start + foundOffset;
        _model.getODDForDocument(_doc).setCurrentLocation(locationToIgnore);
        if (_shouldIgnore(locationToIgnore, _doc)) {
          foundOffset += start;
          if (!_searchBackwards) {
            foundOffset += findWord.length();
            return _findWrapped(foundOffset, docLen-foundOffset);
          }
          return _findWrapped(start, foundOffset-start);
        }       
        

        foundOffset += start;
        if (!_searchBackwards) foundOffset += findWord.length();
        _current = _doc.createPosition(foundOffset);  
      }
      return foundOffset;
    }
    catch (BadLocationException e) { throw new UnexpectedException(e); }
  }   
  
  
  private FindResult _findNextInAllDocs(AbstractDocumentInterface docToSearch, int start, int end) throws BadLocationException {
    _checkAllDocsWrapped = true;    
    while (docToSearch != _doc) {
      if (docToSearch == _firstDoc) {
        _allDocsWrapped = true;
        _checkAllDocsWrapped = false;
      }
      
      String text;
      int docLen;
      docToSearch.acquireReadLock();
      try { 
        docLen = docToSearch.getLength();
        text = docToSearch.getText(start, end);
      }
      finally { docToSearch.releaseReadLock(); }
      String findWord = _findWord;
      if (!_matchCase) {
        text = text.toLowerCase();
        findWord = findWord.toLowerCase();
      }
      int foundOffset = !_searchBackwards ? text.indexOf(findWord) : text.lastIndexOf(findWord);
      if (foundOffset >= 0) {
        int locationToIgnore = start + foundOffset;
        _model.getODDForDocument(docToSearch).setCurrentLocation(locationToIgnore);
        if (_shouldIgnore(locationToIgnore, docToSearch)) {
          foundOffset += start;
          if (!_searchBackwards) {
            foundOffset += findWord.length();
            _current = _doc.createPosition(foundOffset);
            return _findNextInAllDocs(docToSearch, foundOffset, docLen-foundOffset);
          }
          _current = _doc.createPosition(foundOffset);
          return _findNextInAllDocs(docToSearch, start, foundOffset-start);
        }       
            
        
        foundOffset += start;
        if (!_searchBackwards) foundOffset += findWord.length();
        return new FindResult(docToSearch, foundOffset, false, _allDocsWrapped);
      }
      docToSearch = !_searchBackwards ? _docIterator.getNextDocument(docToSearch) :
                                        _docIterator.getPrevDocument(docToSearch);
      start = 0;
      end = docToSearch.getLength();
    }
    return new FindResult(docToSearch, -1, false, _allDocsWrapped);
  } 
  
  
  private boolean wholeWordFoundAtCurrent(AbstractDocumentInterface doc, int foundOffset) {    
    String docText;
    doc.acquireReadLock();
    try { docText = doc.getText(); }
    finally {doc.releaseReadLock();}      
    
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
  
  
  private boolean _shouldIgnore(int foundOffset, AbstractDocumentInterface doc) {
    return (_matchWholeWord && !wholeWordFoundAtCurrent(doc, foundOffset)) || 
      (_ignoreCommentsAndStrings && _model.getODDForDocument(doc).getStateAtCurrent() != ReducedModelStates.FREE);
  }
}