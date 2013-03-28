

package net.sourceforge.pmd.lang.java.ast;

public class ASTAndExpression extends AbstractJavaTypeNode {
    public ASTAndExpression(int id) {
        super(id);
    }

    public ASTAndExpression(JavaParser p, int id) {
        super(p, id);
    }


    
    public Object jjtAccept(JavaParserVisitor visitor, Object data) {
        return visitor.visit(this, data);
    }
}
