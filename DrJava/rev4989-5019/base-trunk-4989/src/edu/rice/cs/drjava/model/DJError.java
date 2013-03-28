

package edu.rice.cs.drjava.model;

import java.io.File;
import java.io.Serializable;

import edu.rice.cs.util.FileOps;
import edu.rice.cs.util.UnexpectedException;



public class DJError implements Comparable<DJError>, Serializable {
  private File _file;
  
  
  private int _lineNumber;
  
  
  private final int _startColumn;
  private final String _message;
  private final boolean _isWarning;
  
  
  private boolean _noLocation;
  
  
  public DJError(File file, int lineNumber, int startColumn, String message, boolean isWarning) {

    if (message != null && (message.indexOf("CompilerError")>=0)) throw new UnexpectedException(message);
    _file = file;
    _lineNumber = lineNumber;
    _startColumn = startColumn;
    _message = message;
    _isWarning = isWarning;
    if (lineNumber < 0) _noLocation = true;
  }
  
  
  public DJError(File file, String message, boolean isWarning) { this(file, -1, -1, message, isWarning); }
  
  
  public DJError(String message, boolean isWarning) { this(null, message, isWarning); }
  
  
  public boolean hasNoLocation() { return _noLocation; }
  
  
  public String toString() {
    return this.getClass().toString() + "(file=" + fileName() + ", line=" + _lineNumber + ", col=" + _startColumn + 
      ", msg=" + _message + ")";
  }
  
  
  public File file() { return _file; }
  
  
  public String fileName() {
    if (_file == null) return "";
    return _file.getAbsolutePath();
  }
  
  
  public int lineNumber() { return  _lineNumber; }
  
  
  public void setLineNumber(int ln) { _lineNumber = ln; }
  
  
  public int startColumn() { return  _startColumn; }
  
  
  public String message() { return  _message; }
  
  
  public String getFileMessage() {
    if (_file == null || _file == FileOps.NULL_FILE) return "(no associated file)";
    return fileName();
  }
  
  
  public String getLineMessage() {
    if (_file == null || _file == FileOps.NULL_FILE || this._lineNumber < 0) return "(no source location)";
    return "" + (_lineNumber + 1);
  }
  
  
  public boolean isWarning() { return  _isWarning; }
  
  
  public int compareTo(DJError other) {
    
    
    if (_file != null) {
      
      if (other.file() != null) {
        
        int fileComp = _file.compareTo(other.file());
        if (fileComp != 0) return fileComp;
        
        return compareByPosition(other);
      }
      else return 1; 
    }
    
    if (other.file() != null) return -1; 
    

    return compareErrorWarning(other);
  }
  
  
  private int compareByPosition(DJError other) {
    
    int byLine = _lineNumber - other.lineNumber();
    if (byLine != 0) return byLine;
    
    int byCol = _startColumn - other.startColumn();
    if (byCol != 0) return byCol;
    return compareErrorWarning(other);
  }
  
  
  private int compareErrorWarning(DJError other) {
    return (isWarning()? (other.isWarning()? 0 : 1) : (other.isWarning()? -1 : 0));
  }
}
