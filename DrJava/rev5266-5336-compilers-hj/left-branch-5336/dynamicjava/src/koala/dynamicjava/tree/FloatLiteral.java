

package koala.dynamicjava.tree;



public class FloatLiteral extends Literal {
    
    public FloatLiteral(String rep) {
 this(rep, SourceInfo.NONE);
    }

    
    public FloatLiteral(String rep, SourceInfo si) {
 super(rep,
       new Float(rep),
       float.class,
       si);
    }
 
}
