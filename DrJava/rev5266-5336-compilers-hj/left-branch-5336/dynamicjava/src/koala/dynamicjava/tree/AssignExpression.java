

package koala.dynamicjava.tree;



public abstract class AssignExpression extends    BinaryExpression
                                       implements StatementExpression {
    
    protected AssignExpression(Expression lexp, Expression rexp,
       SourceInfo si) {
 super(lexp, rexp, si);
    }
}
