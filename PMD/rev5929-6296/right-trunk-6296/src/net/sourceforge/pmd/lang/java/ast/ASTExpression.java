

package net.sourceforge.pmd.lang.java.ast;

public class ASTExpression extends AbstractJavaTypeNode {
    public ASTExpression(int id) {
        super(id);
    }

    public ASTExpression(JavaParser p, int id) {
        super(p, id);
    }


    
    public Object jjtAccept(JavaParserVisitor visitor, Object data) {
        return visitor.visit(this, data);
    }
}
