

package koala.dynamicjava.tree;



public class BooleanLiteral extends Literal {
  
  private final static String TRUE  = "true";
  
  
  private final static String FALSE = "false";
  
  
  public BooleanLiteral(boolean val) {
    this(val, SourceInfo.NONE);
  }
  
  
  public BooleanLiteral(boolean val, SourceInfo si) {
    super(val ? TRUE : FALSE,
          val ? Boolean.TRUE : Boolean.FALSE,
          boolean.class,
          si);
  }

}
