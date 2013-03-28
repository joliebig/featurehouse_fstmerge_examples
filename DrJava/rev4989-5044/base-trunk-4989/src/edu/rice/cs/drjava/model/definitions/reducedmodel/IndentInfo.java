

package edu.rice.cs.drjava.model.definitions.reducedmodel;


public class IndentInfo {
  private String _lineEnclosingBraceType;      

  
  
  private int _distToLineEnclosingBraceStart;   

  
  private int _distToLineEnclosingBrace;  

  private String _enclosingBraceType;  
  
  
  private int _distToEnclosingBraceStart;   

  
  private int _distToEnclosingBrace; 

  
  private int _distToStart; 

  static public final String NONE = "";           
  static public final String OPEN_CURLY = "{";  
  static public final String OPEN_PAREN = "(";    
  static public final String OPEN_BRACKET = "[";  

  
  public IndentInfo() {
    _lineEnclosingBraceType = NONE;
    _distToLineEnclosingBraceStart = -1;
    _distToLineEnclosingBrace = -1;
    _enclosingBraceType = NONE;
    _distToEnclosingBraceStart = -1;
    _distToEnclosingBrace = -1;
    _distToStart = -1;
  }

  
  public IndentInfo(String lineEnclosingBraceType, int distToLineEnclosingBraceStart, int distToLineEnclosingBrace, 
                    int distToStart) {
    _lineEnclosingBraceType = lineEnclosingBraceType;
    _distToLineEnclosingBraceStart = distToLineEnclosingBraceStart;
    _distToLineEnclosingBrace = distToLineEnclosingBrace;
    _distToStart = distToStart;
  }
  
  public String lineEnclosingBraceType() { return _lineEnclosingBraceType; }
  public int distToLineEnclosingBraceStart() { return _distToLineEnclosingBraceStart; }
  public int distToLineEnclosingBrace() { return _distToLineEnclosingBrace; }
  public String enclosingBraceType() { return _enclosingBraceType; }
  public int distToEnclosingBraceStart() { return _distToEnclosingBraceStart; }
  public int distToEnclosingBrace() { return _distToEnclosingBrace; }
  public int distToStart() { return _distToStart; }
  
  public void setLineEnclosingBraceType(String t) { _lineEnclosingBraceType = t; }
  public void setDistToLineEnclosingBraceStart(int d) { _distToLineEnclosingBraceStart = d; }
  public void setDistToLineEnclosingBrace(int d) { _distToLineEnclosingBrace = d; }
  public void setEnclosingBraceType(String t) { _enclosingBraceType = t; }
  public void setDistToEnclosingBraceStart(int d) { _distToEnclosingBraceStart = d; }
  public void setDistToEnclosingBrace(int d) { _distToEnclosingBrace = d; }
  public void setDistToStart(int d) { _distToStart = d; }
  
  public String toString() {
    return "IdentInfo[" + _distToStart + ", " + _lineEnclosingBraceType + ", " + _distToLineEnclosingBrace + ", " +
      _distToLineEnclosingBraceStart + ", " + _enclosingBraceType + ", " + _distToEnclosingBrace + ", " + 
      _distToEnclosingBraceStart + "]";
  }
}

