

package edu.rice.cs.javalanglevels;

import java.io.*;
import edu.rice.cs.javalanglevels.parser.JExprParser;


public final class SourceInfo {
  
   
  public static final SourceInfo NO_INFO = new SourceInfo(null, -1, -1, -1, -1);
  
  
  private final File _file;
  
  
  private final int _startLine;
  
  
  private final int _startColumn;
  
  
  private final int _endLine;
  
  
  private final int _endColumn;

  
  public SourceInfo(File file,
                    int startLine,
                    int startColumn,
                    int endLine,
                    int endColumn)
  {
    _file = file;
    _startLine = startLine;
    _startColumn = startColumn;
    _endLine = endLine;
    _endColumn = endColumn;
  }

  
  final public File getFile() { return _file; }

  
  final public int getStartLine() { return _startLine; }
  
  
  final public int getStartColumn() { return _startColumn; }
  
  
  final public int getEndLine() { return _endLine; }
  
  
  final public int getEndColumn() { return _endColumn; }

  
  public String toString() {
    String fileName;
    if (_file == null) {
      fileName = "(no file)";
    }
    else {
      fileName = _file.getName();
    }

    return "[" + fileName + ": " +
           "(" + _startLine + "," + _startColumn + ")-" +
           "(" + _endLine + "," + _endColumn + ")]";
  }

  
  public boolean equals(Object obj) {
    if (obj == null) return false;

    if (obj.getClass() != this.getClass()) {
      return false;
    }
    else {
      SourceInfo casted = (SourceInfo) obj;

      File tF = getFile();
      File oF = casted.getFile();

      if ( ((tF == null) && (oF != null)) ||
           ((tF != null) && ! tF.equals(oF)))
      {
        return false;
      }

      if (! (getStartLine() == casted.getStartLine())) return false;
      if (! (getStartColumn() == casted.getStartColumn())) return false;
      if (! (getEndLine() == casted.getEndLine())) return false;
      if (! (getEndColumn() == casted.getEndColumn())) return false;
      return true;
    }
  }

  
  public final int hashCode() {
    int code = getClass().hashCode();

    if (getFile() != null) {
      code ^= getFile().hashCode();
    }

    code ^= getStartLine();
    code ^= getStartColumn();
    code ^= getEndLine();
    code ^= getEndColumn();
    return code;
  }
}
