

package edu.rice.cs.drjava.model.repl.newjvm;


import koala.dynamicjava.parser.*;
import koala.dynamicjava.parser.wrapper.*;


public class SyntaxErrorResult implements InterpretResult {
  private final int _startRow;
  private final int _startCol;
  private final int _endRow;
  private final int _endCol;

  private final String _errorMessage;
  private final String _interaction;

  public SyntaxErrorResult(ParseException pe, String s) {
    _startRow = pe.getBeginLine();
    _startCol = pe.getBeginColumn();
    _endRow = pe.getEndLine();
    _endCol = pe.getEndColumn();
    _errorMessage = pe.getShortMessage();
    _interaction = s;
  }

  public SyntaxErrorResult(ParseError pe, String s) {
    ParseException parseEx = pe.getParseException();
    if (parseEx != null) {
      _startRow = parseEx.getBeginLine();
      _startCol = parseEx.getBeginColumn();
      _endRow = parseEx.getEndLine();
      _endCol = parseEx.getEndColumn();
      _errorMessage = parseEx.getShortMessage();      
    }      
    else {
      _startRow = _endRow = pe.getLine();
      _startCol = _endCol = pe.getColumn();
      _errorMessage = pe.getMessage();
    }
    _interaction = s;
  }

  public SyntaxErrorResult(TokenMgrError pe, String s) {
    _endRow = _startRow = pe.getErrorRow();
    
    
    _endCol = _startCol = pe.getErrorColumn() - 1;
    _errorMessage = pe.getMessage();
    _interaction = s;
  }

  public String getErrorMessage() { return _errorMessage; }

  public String getInteraction() { return _interaction; }

  public int getStartRow() { return _startRow; }
  public int getStartCol() { return _startCol; }
  public int getEndRow() { return _endRow; }
  public int getEndCol() { return _endCol; }

  public <T> T apply(InterpretResultVisitor<T> v) { return v.forSyntaxErrorResult(this); }

  public String toString() { return "(syntax error: " + _errorMessage + ")"; }
}