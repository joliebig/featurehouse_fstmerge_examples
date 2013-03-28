

package edu.rice.cs.util.docnavigation;

class DummyINavigatorItem implements INavigatorItem {
  
  private String name;
  DummyINavigatorItem(String s) { name = s; }
  public boolean checkIfClassFileInSync() { 
    throw new UnsupportedOperationException("checkIfClassFileInSync() not implemented"); 
  }
  public String getName() { return name; }
  public void setName(String s) { name = s; }
  
  public boolean fileExists() { throw new UnsupportedOperationException("fileExists() not implemented"); }
  public boolean isAuxiliaryFile() { throw new UnsupportedOperationException("isAuxiliaryFile() not implemented"); }
  public boolean inProject() { throw new UnsupportedOperationException("inProject() not implemented"); }
  public boolean isUntitled() { throw new UnsupportedOperationException("isUntitled() not implemented"); }
  public String toString() { return getName(); }
}