

package koala.dynamicjava.tree;



public class DoubleLiteral extends Literal {
    
    public DoubleLiteral(String rep) {
 this(rep, SourceInfo.NONE);
    }

    
    public DoubleLiteral(String rep, SourceInfo si) {
 super(rep,
       new Double(rep),
       double.class,
       si);
    }

}
