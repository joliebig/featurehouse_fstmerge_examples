

package net.sourceforge.pmd.lang.java.ast;

public class ASTExclusiveOrExpression extends AbstractJavaTypeNode {
    public ASTExclusiveOrExpression(int id) {
        super(id);
    }

    public ASTExclusiveOrExpression(JavaParser p, int id) {
        super(p, id);
    }


    
    public Object jjtAccept(JavaParserVisitor visitor, Object data) {
        return visitor.visit(this, data);
    }
}
