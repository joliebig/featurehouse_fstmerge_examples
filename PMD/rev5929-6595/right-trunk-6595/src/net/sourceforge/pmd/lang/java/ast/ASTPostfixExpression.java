

package net.sourceforge.pmd.lang.java.ast;

public class ASTPostfixExpression extends AbstractJavaTypeNode {

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
