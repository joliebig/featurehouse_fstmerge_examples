

package net.sourceforge.pmd.ast;

public class ASTPostfixExpression extends SimpleJavaTypeNode {

    public ASTPostfixExpression(int id) {
        super(id);
    }

    public ASTPostfixExpression(JavaParser p, int id) {
        super(p, id);
    }

    
    public Object jjtAccept(JavaParserVisitor visitor, Object data) {
        return visitor.visit(this, data);
    }

}
