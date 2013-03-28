

package net.sourceforge.pmd.lang.java.ast;

public class ASTPreDecrementExpression extends AbstractJavaTypeNode {
    public ASTPreDecrementExpression(int id) {
        super(id);
    }

    public ASTPreDecrementExpression(JavaParser p, int id) {
        super(p, id);
    }


    
    public Object jjtAccept(JavaParserVisitor visitor, Object data) {
        return visitor.visit(this, data);
    }
}
