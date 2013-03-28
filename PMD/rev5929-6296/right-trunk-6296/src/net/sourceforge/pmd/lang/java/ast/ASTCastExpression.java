

package net.sourceforge.pmd.lang.java.ast;

public class ASTCastExpression extends AbstractJavaTypeNode {
    public ASTCastExpression(int id) {
        super(id);
    }

    public ASTCastExpression(JavaParser p, int id) {
        super(p, id);
    }


    
    public Object jjtAccept(JavaParserVisitor visitor, Object data) {
        return visitor.visit(this, data);
    }
}
