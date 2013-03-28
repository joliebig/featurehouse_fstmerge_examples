

package koala.dynamicjava.tree;



public class NullLiteral extends Literal {
    
    public NullLiteral() {
 this(SourceInfo.NONE);
    }

    
    public NullLiteral(SourceInfo si) {
 super("null", null, null, si);
    }
 
}
