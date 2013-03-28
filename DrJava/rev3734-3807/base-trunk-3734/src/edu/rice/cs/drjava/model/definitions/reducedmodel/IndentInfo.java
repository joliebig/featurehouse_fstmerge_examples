

package edu.rice.cs.drjava.model.definitions.reducedmodel;


public class IndentInfo {
  public String braceType;      

  
  
  
  public int distToNewline;

  
  public int distToBrace;

  
  public String braceTypeCurrent;

  
  public int distToNewlineCurrent;

  
  public int distToBraceCurrent;

  
  public int distToPrevNewline;

  static public final String noBrace = "";
  static public final String openSquiggly = "{";
  static public final String openParen = "(";
  static public final String openBracket = "[";

  
  public IndentInfo() {
    braceType = noBrace;
    distToNewline = -1;
    distToBrace = -1;
    braceTypeCurrent = noBrace;
    distToNewlineCurrent = -1;
    distToBraceCurrent = -1;
  }

  
  public IndentInfo(String _braceType, int _distToNewline, int _distToBrace, int _distToPrevNewline) {
    braceType = _braceType;
    distToNewline = _distToNewline;
    distToBrace = _distToBrace;
    distToPrevNewline = _distToPrevNewline;
  }
}



