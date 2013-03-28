

package edu.rice.cs.drjava.model.compiler;

import java.io.File;
import java.io.Serializable;


public class CompilerError implements Comparable, Serializable {
  private File _file;
  
  
  private int _lineNumber;
  
  
  private int _startColumn;
  private String _message;
  private boolean _isWarning;
  
  
  private boolean _noLocation;
  
  
  public CompilerError(File file, int lineNumber, int startColumn, String message, boolean isWarning) {
    _file = file;
    _lineNumber = lineNumber;
    _startColumn = startColumn;
    _message = message;
    _isWarning = isWarning;
    if (lineNumber < 0) _noLocation = true;
  }
    
  
  public CompilerError(File file, String message, boolean isWarning) { this(file, -1, -1, message, isWarning); }
  
  
  public CompilerError(String message, boolean isWarning) { this(null, message, isWarning); }
  
  
  public boolean hasNoLocation() { return _noLocation; }
  
  
  public String toString() {
    return this.getClass().toString() + "(file=" + fileName() + ", line=" +
      _lineNumber + ", col=" + _startColumn + ", msg=" + _message + ")";
  }
  
  
  public File file() { return _file; }
  
  
  public String fileName() {
    if (_file == null) return "";
    return _file.getAbsolutePath();
  }
  
  
  public int lineNumber() { return  _lineNumber; }
  
  
  public int startColumn() { return  _startColumn; }
  
  
  public String message() { return  _message; }
  
  
  public String getFileMessage() {
    if (_file == null) return "(no associated file)";
    return fileName();
  }
  
  
  public String getLineMessage() {
    if (_file == null || this._lineNumber < 0) return "(no source location)";
    return "" + (_lineNumber + 1);
  }
  
  
  public boolean isWarning() { return  _isWarning; }
  
  
  public int compareTo(Object o) {
    CompilerError other = (CompilerError) o;
    
    
    if (_file != null) {
      if (other.file() == null)
        
        return 1;
      else {
        
        int fileComp = _file.compareTo(other.file());
        if (fileComp != 0) return fileComp;
        
        return compareByPosition(other);
      }
    }
    
    if (other.file() == null) {
      
      
      return (this.isWarning() ? (other.isWarning() ? 0 : 1) : (other.isWarning()? -1:0));
    }
    else return -1; 
  }
  
  
  private int compareByPosition(CompilerError other) {
    
    if (_lineNumber == other._lineNumber) {
      int byCol = _startColumn - other._startColumn;
      
      return (this.isWarning()? (other.isWarning()? byCol:1):(other.isWarning()? -1:byCol));
    }
    else return  _lineNumber - other._lineNumber;
  }
  
}