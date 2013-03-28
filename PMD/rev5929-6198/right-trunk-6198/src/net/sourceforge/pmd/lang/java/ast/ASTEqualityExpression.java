

package net.sourceforge.pmd.lang.java.ast;

public class ASTEqualityExpression extends AbstractJavaTypeNode {
    public ASTEqualityExpression(int id) {
        super(id);
    }

    public ASTEqualityExpression(JavaParser p, int id) {
        super(p, id);
    }

    
    public Object jjtAccept(JavaParserVisitor visitor, Object data) {
        return visitor.visit(this, data);
    }

}
