

package net.sourceforge.pmd.ast;

public class ASTUnaryExpression extends SimpleJavaTypeNode {
    public ASTUnaryExpression(int id) {
        super(id);
    }

    public ASTUnaryExpression(JavaParser p, int id) {
        super(p, id);
    }

    
    public Object jjtAccept(JavaParserVisitor visitor, Object data) {
        return visitor.visit(this, data);
    }

}
