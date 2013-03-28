
package genj.gedcom;



public abstract class PropertyVisitor {
  
  
  protected boolean leaf(Property leaf) {
    return true;
  }

  
  protected boolean recursion(Property parent, String child) {
    return true;
  }

} 
