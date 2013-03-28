

package koala.dynamicjava.tree;



public class Identifier implements IdentifierToken {

  private final String image;
  private final SourceInfo sourceInfo;
  
  
  public Identifier(String im) {
    this(im, SourceInfo.NONE);
  }
  
  
  public Identifier(String im, SourceInfo si) {
    image       = im;
    sourceInfo  = si;
  }
  
  
  public String image() {
    return image;
  }
  
  public SourceInfo getSourceInfo() {
    return sourceInfo;
  }

  
  public String toString() {
    return "("+getClass().getName()+": "+image()+")";
  }
}
