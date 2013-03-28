

package net.sourceforge.pmd.lang.java.ast;

public class ASTUnaryExpressionNotPlusMinus extends AbstractJavaTypeNode {
    public ASTUnaryExpressionNotPlusMinus(int id) {
        super(id);
    }

    public ASTUnaryExpressionNotPlusMinus(JavaParser p, int id) {
        super(p, id);
    }

    
    public Object jjtAccept(JavaParserVisitor visitor, Object data) {
        return visitor.visit(this, data);
    }

}
