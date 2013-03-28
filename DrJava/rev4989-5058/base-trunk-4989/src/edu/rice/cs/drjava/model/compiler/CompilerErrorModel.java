

package edu.rice.cs.drjava.model.compiler;

import java.io.File;
import java.io.IOException;
import javax.swing.text.*;
import java.util.Arrays;
import java.util.List;
import java.util.LinkedList;
import java.util.HashMap;

import edu.rice.cs.util.FileOps;
import edu.rice.cs.util.StringOps;
import edu.rice.cs.util.UnexpectedException;
import edu.rice.cs.drjava.model.DJError;
import edu.rice.cs.drjava.model.DummyGlobalModel;
import edu.rice.cs.drjava.model.GlobalModel;
import edu.rice.cs.util.OperationCanceledException;
import edu.rice.cs.drjava.model.OpenDefinitionsDocument;
import edu.rice.cs.drjava.model.FileMovedException;


public class CompilerErrorModel {
  private static final String newLine = StringOps.EOL;
  
  private final DJError[] _errors;
  
  
  private final Position[] _positions;
  
  
  private final int _numErrors;
  
  
  private int _numCompilerErrors;
  
  
  private int _numWarnings;
  
  
  
  private int _onlyWarnings = -1;
  
  
  private final HashMap<File, StartAndEndIndex> _filesToIndexes = new HashMap<File, StartAndEndIndex>();
  
  
  private final GlobalModel _model;
  
  
  public CompilerErrorModel() {
    _model = new DummyGlobalModel() {
      public OpenDefinitionsDocument getDocumentForFile(File file) {
        throw new IllegalStateException("No documents to get!");
      }
      public boolean isAlreadyOpen(File file) { return false; }
      public List<OpenDefinitionsDocument> getOpenDefinitionsDocuments() {
        return new LinkedList<OpenDefinitionsDocument>();
      }
      public boolean hasModifiedDocuments() { return false; }
      public boolean hasUntitledDocuments() { return false; }
    };
    _errors = new DJError[0];
    _numErrors = 0;
    _numWarnings = 0;
    _numCompilerErrors = 0;
    _positions = new Position[0];
  }
  
  
  public CompilerErrorModel(DJError[] errors, GlobalModel model) {
    

    _model = model;
    
    
    _errors = errors;
    
    _numErrors = errors.length;
    _positions = new Position[errors.length];
    
    _numWarnings = 0;
    _numCompilerErrors = 0;
    for (int i =0; i < errors.length; i++){
      if (errors[i].isWarning()) _numWarnings++;
      else _numCompilerErrors++;
    }
    
    
    Arrays.sort(_errors);
    
    
    _calculatePositions();
  }
  
  
  public DJError getError(int idx) { return _errors[idx]; }
  
  
  public Position getPosition(DJError error) {
    int spot = Arrays.binarySearch(_errors, error);
    return _positions[spot];
  }
  
  
  public int getNumErrors() { return _numErrors; }
  
  
  public int getNumCompErrors() { return _numCompilerErrors; }
  
  
  public int getNumWarnings() { return _numWarnings; }
  
  
  public String toString() {
    final StringBuilder buf = new StringBuilder();
    buf.append(this.getClass().toString() + ":\n  ");
    for (int i=0; i < _numErrors; i++) {
      buf.append(_errors[i].toString());
      buf.append("\n  ");
    }
    return buf.toString();
  }
  
  
  public DJError getErrorAtOffset(OpenDefinitionsDocument odd, int offset) {
    File file;
    try { 
      file = odd.getFile(); 
      if (file == null) return null;
    }
    catch (FileMovedException e) { file = e.getFile(); }
    
    
    try { file = file.getCanonicalFile(); }
    catch (IOException ioe) {
      
    }
    
    StartAndEndIndex saei = _filesToIndexes.get(file);
    if (saei == null) return null;
    int start = saei.getStartPos();
    int end = saei.getEndPos();
    if (start == end) return null;
    
    
    
    
    int errorAfter; 
    for (errorAfter = start; errorAfter < end; errorAfter++) {
      if (_positions[errorAfter] == null) {
        
        return null;
      }
      if (_positions[errorAfter].getOffset() >=offset) break;
    }
    
    
    int errorBefore = errorAfter - 1;
    
    
    int shouldSelect = -1;
    
    if (errorBefore >= start) { 
      int errPos = _positions[errorBefore].getOffset();
      try {
        String betweenDotAndErr = odd.getText(errPos, offset - errPos);
        if (betweenDotAndErr.indexOf('\n') == -1) shouldSelect = errorBefore;
      }
      catch (BadLocationException e) {  }
      catch (StringIndexOutOfBoundsException e) {  }
    }
    
    if ((shouldSelect == -1) && (errorAfter < end)) {
      
      
      
      int errPos = _positions[errorAfter].getOffset();
      try {
        String betweenDotAndErr = odd.getText(offset, errPos - offset);
        if (betweenDotAndErr.indexOf('\n') == -1) shouldSelect = errorAfter;
      }
      catch (BadLocationException e) {  }
      catch (StringIndexOutOfBoundsException e) {  }
    }
    
    if (shouldSelect == -1) return null;
    return _errors[shouldSelect];
  }
  
  
  public boolean hasErrorsWithPositions(OpenDefinitionsDocument odd) {
    File file = FileOps.NULL_FILE;
    try { 
      file = odd.getFile();
      if (file == null || file == FileOps.NULL_FILE) return false;
    }
    catch (FileMovedException fme) { file = fme.getFile(); }
    
    
    try { file = file.getCanonicalFile(); }
    catch (IOException ioe) {  }
    
    StartAndEndIndex saei = _filesToIndexes.get(file);
    if (saei == null) return false;
    if (saei.getStartPos() == saei.getEndPos()) return false;
    return true;
  }
  
  
  public boolean hasOnlyWarnings() {
    
    if (_onlyWarnings == 0) return false;
    if (_onlyWarnings == 1) return true;
    else {
      
      boolean clean = true;
      for (int i = 0; clean && (i < _numErrors); i++) {
        clean = _errors[i].isWarning();
      }
      
      _onlyWarnings = clean? 1: 0;
      return clean;
    }
  }
  
  
  private void _calculatePositions() {
    try {
      int curError = 0;
      
      
      while ((curError < _numErrors)) {
        
        curError = nextErrorWithLine(curError);
        if (curError >= _numErrors) {break;} 
        
        
        File file = _errors[curError].file();
        OpenDefinitionsDocument document;
        try { document = _model.getDocumentForFile(file); }
        catch (Exception e) {
          
          if ((e instanceof IOException) || (e instanceof OperationCanceledException)) {
            
            do { curError++;} 
            while ((curError < _numErrors) && (_errors[curError].file().equals(file)));
            
            
            continue;
          }
          else throw new UnexpectedException(e);
        }
        if (curError >= _numErrors) break;
        
        
        final int fileStartIndex = curError;
        final int defsLength = document.getLength();
        final String defsText = document.getText(0, defsLength);
        int curLine = 0;
        int offset = 0; 
        
        
        
        while ((curError < _numErrors) && 
               file.equals(_errors[curError].file()) &&  
               (offset <= defsLength)) { 
          
          boolean didNotAdvance = false;
          if (_errors[curError].lineNumber() != curLine) {
            
            
            
            
            didNotAdvance = true;
          }
          else {
            while ((curError < _numErrors) &&
                   file.equals(_errors[curError].file()) &&  
                   (_errors[curError].lineNumber() == curLine)) {
              _positions[curError] = document.createPosition(offset + _errors[curError].startColumn());
              curError++;
            }
          }
          
          
          
          
          
          if (curError < _numErrors) {
            int curErrorLine = _errors[curError].lineNumber();
            int nextNewline = 0;
            while ((curLine != curErrorLine)
                     && (nextNewline != -1)
                     && (file.equals(_errors[curError].file()))) {
              nextNewline = defsText.indexOf(newLine, offset);
              if (nextNewline == -1) nextNewline = defsText.indexOf("\n", offset);
              if (nextNewline != -1) {
                curLine++;
                offset = nextNewline + 1;
              }
              else {
                
                if (didNotAdvance) {
                  
                  
                  
                  
                  
                  
                  
                  _positions[curError] = null;
                  curError++;
                }
              }
            }
          }
        }
        
        
        
        int fileEndIndex = curError;
        if (fileEndIndex != fileStartIndex) {
          
          try { file = file.getCanonicalFile(); }
          catch (IOException ioe) {  }
          _filesToIndexes.put(file, new StartAndEndIndex(fileStartIndex, fileEndIndex));
        }
      }
    }
    catch (BadLocationException ble) { throw new UnexpectedException(ble); }
    catch (StringIndexOutOfBoundsException e) { throw new UnexpectedException(e); }
  }
  
  
  private int nextErrorWithLine(int idx) {
    while (idx < _numErrors && (_errors[idx].hasNoLocation() || _errors[idx].file() == null)) idx++;
    return idx;
  }
  
  
  private static class StartAndEndIndex {
    private int startPos;
    private int endPos;
    
    public StartAndEndIndex(int startPos, int endPos) {
      this.startPos = startPos;
      this.endPos = endPos;
    }
    public int getStartPos() { return startPos; }
    public int getEndPos() { return endPos; }
  }
}
