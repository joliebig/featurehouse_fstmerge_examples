

package net.sourceforge.pmd.ast;

public class ASTUnaryExpressionNotPlusMinus extends SimpleJavaTypeNode {
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
