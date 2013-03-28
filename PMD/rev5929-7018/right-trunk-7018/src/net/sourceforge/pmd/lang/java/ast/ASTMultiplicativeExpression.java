

package net.sourceforge.pmd.lang.java.ast;

public class ASTMultiplicativeExpression extends AbstractJavaTypeNode {
    public ASTMultiplicativeExpression(int id) {
        super(id);
    }

    public ASTMultiplicativeExpression(JavaParser p, int id) {
        super(p, id);
    }


    
    public Object jjtAccept(JavaParserVisitor visitor, Object data) {
        return visitor.visit(this, data);
    }
}
