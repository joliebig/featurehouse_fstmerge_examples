

package edu.rice.cs.drjava.model.definitions.reducedmodel;


public class BraceInfo {
  
  
  
  public static final String NONE = "";             
  public static final String OPEN_CURLY = "{";      
  public static final String OPEN_PAREN    = "(";   
  public static final String OPEN_BRACKET  = "[";   

  public static final BraceInfo NULL = new BraceInfo(NONE, -1);
  
  private String _braceType;   

  
  private int _distance; 

  
  public BraceInfo(String braceType, int distance) {
    _braceType = braceType;
    _distance = distance;
  }

  
  public String braceType() { return _braceType; }
  
  
  public int distance() { return _distance; }
  
  
  public BraceInfo shift(int dist) { 
    if (this == NULL) return NULL;
    return new BraceInfo(_braceType, _distance + dist); 
  }
  
  public String toString() { return "BraceInfo(" + _distance + ", '" + _braceType + "')"; }
}



