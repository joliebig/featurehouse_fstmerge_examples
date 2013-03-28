

package edu.rice.cs.drjava.model;   

import edu.rice.cs.plt.lambda.Runnable1;
import edu.rice.cs.util.UnexpectedException;
import edu.rice.cs.util.swing.DocumentIterator;
import edu.rice.cs.util.swing.Utilities;
import edu.rice.cs.util.Log;
import edu.rice.cs.util.StringOps;

import java.awt.EventQueue;

import javax.swing.text.BadLocationException;


public class FindReplaceMachine {
  
  static private Log _log = new Log("FindReplace.txt", false);
  
  
  private OpenDefinitionsDocument _doc;      
  private OpenDefinitionsDocument _firstDoc; 

  private int _current;                 
  private MovingDocumentRegion _selectionRegion; 

  private String _findWord;                  
  private String _replaceWord;               
  private boolean _matchCase;
  private boolean _matchWholeWord;
  private boolean _searchAllDocuments;       
  private boolean _searchSelectionOnly;      
  private boolean _isForward;                
  private boolean _ignoreCommentsAndStrings; 
  private boolean _ignoreTestCases;          
  private String _lastFindWord;              
  private boolean _skipText;                 
  private DocumentIterator _docIterator;     
  private SingleDisplayModel _model;
  
  
  public FindReplaceMachine(SingleDisplayModel model, DocumentIterator docIterator) {    
    _skipText = false;


    _model = model;
    _docIterator = docIterator;
    _current = -1;
    setFindAnyOccurrence();
    setFindWord("");
    setReplaceWord("");
    setSearchBackwards(false);
    setMatchCase(true);
    setSearchAllDocuments(false);
    setSearchSelectionOnly(false);
    setIgnoreCommentsAndStrings(false);
    setIgnoreTestCases(false);
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
  
  public boolean isSearchBackwards() { return ! _isForward; }
  
  public void setSearchBackwards(boolean searchBackwards) {
    if (_isForward == searchBackwards) {
      
      
      if (onMatch() && _findWord.equals(_lastFindWord)) _skipText = true;
      else _skipText = false;
    }
    _isForward = ! searchBackwards;
  }
  
  public void setMatchCase(boolean matchCase) { _matchCase = matchCase; }
  public boolean getMatchCase() { return _matchCase; }
  
  public void setMatchWholeWord() { _matchWholeWord = true; }
  
  public boolean getMatchWholeWord() { return _matchWholeWord; }
  
  public void setFindAnyOccurrence() { _matchWholeWord = false; }  
  
  public void setSearchAllDocuments(boolean searchAllDocuments) { _searchAllDocuments = searchAllDocuments; }
  
  public void setSearchSelectionOnly(boolean searchSelectionOnly) { _searchSelectionOnly = searchSelectionOnly; }
  
  public void setIgnoreCommentsAndStrings(boolean ignoreCommentsAndStrings) {
    _ignoreCommentsAndStrings = ignoreCommentsAndStrings;
  }
  public boolean getIgnoreCommentsAndStrings() { return _ignoreCommentsAndStrings; }
  
  public void setIgnoreTestCases(boolean ignoreTestCases) {
    _ignoreTestCases = ignoreTestCases;
  }
  public boolean getIgnoreTestCases() { return _ignoreTestCases; }

  public void setDocument(OpenDefinitionsDocument doc) { _doc = doc; }
  
  public void setFirstDoc(OpenDefinitionsDocument firstDoc) { _firstDoc = firstDoc; }
  
  public void setPosition(int pos) { _current = pos; }
  
  
  public int getCurrentOffset() { 
    return _current;
  }
  
  public String getFindWord() { return _findWord; }
  
  public String getReplaceWord() { return _replaceWord; }
  
  public boolean getSearchAllDocuments() { return _searchAllDocuments; }
  
  public boolean getSearchSelectionOnly() { return _searchSelectionOnly; }
  
  public OpenDefinitionsDocument getDocument() { return _doc; }
  
  public OpenDefinitionsDocument getFirstDoc() { return _firstDoc; }
  
  
  public void setFindWord(String word) {  
    _findWord = StringOps.replace(word, StringOps.EOL, "\n"); 
  }
  
  
  public void setReplaceWord(String word) { 
    _replaceWord = StringOps.replace(word, StringOps.EOL,"\n"); 
  }
  
  
  public boolean onMatch() {
    
    

    
    String findWord = _findWord;
    int wordLen, off;
    
    if(_current == -1) return false;
    
    wordLen = findWord.length();
    if (_isForward) off = getCurrentOffset() - wordLen;
    else off = getCurrentOffset();
    
    if (off < 0) return false;
    
    String matchSpace;
    try {
      if (off + wordLen > _doc.getLength()) return false;
      matchSpace = _doc.getText(off, wordLen);
    }
    catch (BadLocationException e) { throw new UnexpectedException(e); }
    
    if (!_matchCase) {
      matchSpace = matchSpace.toLowerCase();
      findWord = findWord.toLowerCase();
    }
    return matchSpace.equals(findWord);
  }
  
  
  public boolean replaceCurrent() {
    
    assert EventQueue.isDispatchThread();
    
    if (! onMatch()) return false;
    try {

      int offset = getCurrentOffset();
      if (_isForward) offset -= _findWord.length();  

      

      
      _doc.remove(offset, _findWord.length());
      

      _doc.insertString(offset, _replaceWord, null);  
      
      
      if (_isForward) setPosition(offset + _replaceWord.length());
      else setPosition(offset);
      
      return true;
    }
    catch (BadLocationException e) { throw new UnexpectedException(e); }
  }

  
  public void setSelection(MovingDocumentRegion s) { 
    _selectionRegion = s;
  }

  
  public int replaceAll() { 
    return replaceAll(_searchAllDocuments, _searchSelectionOnly); 
  }
  
  
  private int replaceAll(boolean searchAll, boolean searchSelectionOnly) {
    if (searchAll) {
      int count = 0;           
      int n = _docIterator.getDocumentCount();
      for (int i = 0; i < n; i++) {
        
        count += _replaceAllInCurrentDoc(false);
        _doc = _docIterator.getNextDocument(_doc);
      }
      
      
      _model.getDocumentNavigator().repaint();
      
      return count;
    }
    else if(searchSelectionOnly) {
      int count = 0;
      count += _replaceAllInCurrentDoc(searchSelectionOnly);
      return count;
    }
    else 
      return _replaceAllInCurrentDoc(false);
  }
  
  
  private int _replaceAllInCurrentDoc(boolean searchSelectionOnly) {
    
    assert EventQueue.isDispatchThread();
    
    if(!searchSelectionOnly) {
      _selectionRegion = new MovingDocumentRegion(_doc, 0, _doc.getLength(),
                                                  _doc._getLineStartPos(0),
                                                  _doc._getLineEndPos(_doc.getLength()));
    }
    if (_isForward) setPosition(_selectionRegion.getStartOffset());
    else setPosition(_selectionRegion.getEndOffset());
    
    int count = 0;
    FindResult fr = findNext(false);  
    
    
    while (!fr.getWrapped() && fr.getFoundOffset()<=_selectionRegion.getEndOffset()) {
      replaceCurrent();
      count++;
      
      fr = findNext(false);           
      
    }
    return count;
  }
  
  
  public int processAll(Runnable1<FindResult> findAction, MovingDocumentRegion region) { 
    _selectionRegion = region;
    return processAll(findAction, _searchAllDocuments, _searchSelectionOnly); 
  }
  
  
  private int processAll(Runnable1<FindResult> findAction, boolean searchAll, boolean searchSelectionOnly) {
    
    assert EventQueue.isDispatchThread();
    
    if (searchAll) {
      int count = 0;           
      int n = _docIterator.getDocumentCount();
      for (int i = 0; i < n; i++) {
        
        count += _processAllInCurrentDoc(findAction, false);
        _doc = _docIterator.getNextDocument(_doc);
      }
      
      
      _model.getDocumentNavigator().repaint();
      
      return count;
    }
    else if(searchSelectionOnly) {
      int count = 0;
      count += _processAllInCurrentDoc(findAction, searchSelectionOnly);
      return count;
    }
    else return _processAllInCurrentDoc(findAction, false);
  }
  
  
  private int _processAllInCurrentDoc(Runnable1<FindResult> findAction, boolean searchSelectionOnly) {
    if(!searchSelectionOnly) {
      _selectionRegion = new MovingDocumentRegion(_doc, 0, _doc.getLength(),
                                                  _doc._getLineStartPos(0),
                                                  _doc._getLineEndPos(_doc.getLength()));
    }
    if (_isForward) setPosition(_selectionRegion.getStartOffset());
    else setPosition(_selectionRegion.getEndOffset());
    
    int count = 0;
    FindResult fr = findNext(false);  
    
    while (! fr.getWrapped() && fr.getFoundOffset()<=_selectionRegion.getEndOffset()) {
      findAction.run(fr);
      count++;
      fr = findNext(false);           
    }
    return count;
  }
  
  public FindResult findNext() { return findNext(_searchAllDocuments); }
  
  
  private FindResult findNext(boolean searchAll) {
    
    assert EventQueue.isDispatchThread();
    
    
    FindResult fr;
    int start;
    int len;
    
    
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
    if (fr.getFoundOffset() >= 0 || ! searchAll) return fr;  
    
    
    return _findNextInOtherDocs(_doc, start, len);
  }
  
  
  
  private FindResult _findNextInDoc(OpenDefinitionsDocument doc, int start, int len, boolean searchAll) {
    


    FindResult fr = _findNextInDocSegment(doc, start, len);
    if (fr.getFoundOffset() >= 0 || searchAll) return fr;
    
    return _findWrapped(doc, start, len, false);  
  }
  
    
  private FindResult _findWrapped(OpenDefinitionsDocument doc, int start, int len, boolean allWrapped) {
    
    final int docLen = doc.getLength();
    if (docLen == 0) return new FindResult(doc, -1, true, allWrapped); 
    
    final int wordLen =  _findWord.length();
    
    assert (start >= 0 && start <= docLen) && (len >= 0 && len <= docLen) && wordLen > 0;
    assert (_isForward && start + len == docLen) || (! _isForward && start == 0);




    
    int newLen;
    int newStart;
    
    final int adjustment = wordLen - 1; 
    
    if (_isForward) {
      newStart = 0;
      newLen = start + adjustment;  
      if (newLen > docLen) newLen = docLen;
    }
    else {
      newStart = len - adjustment;
      if (newStart < 0) newStart = 0;
      newLen = docLen - newStart;
    }
    


    return _findNextInDocSegment(doc, newStart, newLen, true, allWrapped);
  } 
  
    
  private FindResult _findNextInDocSegment(OpenDefinitionsDocument doc, int start, int len) {
    return _findNextInDocSegment(doc, start, len, false, false);
  }
  
  
  private FindResult _findNextInDocSegment(final OpenDefinitionsDocument doc, final int start, int len, 
                                           final boolean wrapped, final boolean allWrapped) {  

    boolean inTestCase = (doc.getFileName().endsWith("Test.java"));
    
    if (!_ignoreTestCases || ! inTestCase) {
      final int docLen = doc.getLength();;     
      final int wordLen = _findWord.length();   
      
      assert (start >= 0 && start <= docLen) && (len >= 0 && len <= docLen);
      
      if (len == 0 || docLen == 0) return new FindResult(doc, -1, wrapped, allWrapped);
      
      if (start + len > docLen) len = docLen - start;
      

      
      String text;             
      final String findWord;   
      
      try { 
        

        text = doc.getText(start, len);
        
        if (! _matchCase) {
          text = text.toLowerCase();
          findWord = _findWord.toLowerCase();  
        }
        else findWord = _findWord;

        
        
        
        
        
        
        
        int foundOffset = _isForward? 0 : len;
        int rem = len;

        while (rem >= wordLen) {
          
          
          foundOffset = _isForward ? text.indexOf(findWord, foundOffset) : text.lastIndexOf(findWord, foundOffset);

          if (foundOffset < 0) break;  
          int foundLocation = start + foundOffset;
          int matchLocation;
          
          if (_isForward) {
            foundOffset += wordLen;                          

            rem = len - foundOffset;                         
            matchLocation = foundLocation + wordLen;         

          }
          else { 
            
            foundOffset -= wordLen;                        
            rem = foundOffset;                             
            matchLocation = foundLocation;                 


          }

          

          assert foundLocation > -1;
          if (_shouldIgnore(foundLocation, doc)) continue;
          
          
          setPosition(matchLocation);
          

          
          return new FindResult(doc, matchLocation, wrapped, allWrapped);  
        }
      }
      catch (BadLocationException e) { throw new UnexpectedException(e); }
    }      
    
    return new FindResult(doc, -1, wrapped, allWrapped);
  }
  
  
  private FindResult _findNextInOtherDocs(final OpenDefinitionsDocument startDoc, int start, int len) {
    

    
    boolean allWrapped = false;
    _doc = _isForward ? _docIterator.getNextDocument(startDoc) : _docIterator.getPrevDocument(startDoc);
    
    while (_doc != startDoc) {
      if (_doc == _firstDoc) allWrapped = true;
      boolean inTestCase = (_doc.getFileName().endsWith("Test.java"));
      
      if (! _ignoreTestCases || ! inTestCase) {

        


        
        
        
        FindResult fr;
        fr = _findNextInDocSegment(_doc, 0, _doc.getLength(), false, allWrapped); 
        
        if (fr.getFoundOffset() >= 0) return fr;
      }

      _doc = _isForward ? _docIterator.getNextDocument(_doc) : _docIterator.getPrevDocument(_doc);     

    }
    
    
    return _findWrapped(startDoc, start, len, true);  
  } 
  
  
  private boolean wholeWordFoundAtCurrent(OpenDefinitionsDocument doc, int foundOffset) {    
    
    char leftOfMatch = 0;   
    char rightOfMatch = 0;  
    int leftLoc = foundOffset - 1;
    int rightLoc = foundOffset + _findWord.length();
    boolean leftOutOfBounds = false;
    boolean rightOutOfBounds = false;
    
    try { leftOfMatch = doc.getText(leftLoc, 1).charAt(0); }
    catch (BadLocationException e) { leftOutOfBounds = true; } 
    catch (IndexOutOfBoundsException e) { leftOutOfBounds = true; }
    try { rightOfMatch = doc.getText(rightLoc, 1).charAt(0); }
    catch (BadLocationException e) { rightOutOfBounds = true; } 
    catch (IndexOutOfBoundsException e) { rightOutOfBounds = true; }    
    
    if (! leftOutOfBounds && ! rightOutOfBounds) return isDelimiter(rightOfMatch) && isDelimiter(leftOfMatch);
    if (! leftOutOfBounds) return isDelimiter(leftOfMatch);
    if (! rightOutOfBounds) return isDelimiter(rightOfMatch);
    return true;
  }
  
  
  private boolean isDelimiter(char ch) { return ! Character.isLetterOrDigit(ch)  &&  ch != '_'; }
  
  
  private boolean _shouldIgnore(int foundOffset, OpenDefinitionsDocument odd) {
    
    assert EventQueue.isDispatchThread();

    return (_matchWholeWord && ! wholeWordFoundAtCurrent(odd, foundOffset)) || 
      (_ignoreCommentsAndStrings && odd.isShadowed(foundOffset));
  }
}
