

package edu.rice.cs.drjava.model;


public class FindResult {
  private OpenDefinitionsDocument _document;
  private int _foundoffset;
  private boolean _wrapped;
  private boolean _allWrapped;
  
  
  public FindResult(OpenDefinitionsDocument document, int foundoffset, boolean wrapped, boolean allWrapped) {
    _document = document;
    _foundoffset = foundoffset;
    _wrapped = wrapped;
    _allWrapped = allWrapped;
  }
  
  
  public String toString() {
    return "FindResult(" + _document + ", " + _foundoffset + ", " + _wrapped + ", " + _allWrapped + ")";
  }
  
  
  public OpenDefinitionsDocument getDocument() { return _document; }
  
   
  public int getFoundOffset() { return _foundoffset; }
  
  
  public boolean getWrapped() { return _wrapped; }
  
  
  public boolean getAllWrapped() { return _allWrapped; }
}
