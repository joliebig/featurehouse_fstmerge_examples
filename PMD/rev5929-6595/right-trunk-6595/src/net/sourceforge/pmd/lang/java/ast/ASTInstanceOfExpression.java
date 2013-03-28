

package net.sourceforge.pmd.lang.java.ast;

public class ASTInstanceOfExpression extends AbstractJavaTypeNode {
    public ASTInstanceOfExpression(int id) {
        super(id);
    }

    public ASTInstanceOfExpression(JavaParser p, int id) {
        super(p, id);
    }


    
    public Object jjtAccept(JavaParserVisitor visitor, Object data) {
        return visitor.visit(this, data);
    }
}
