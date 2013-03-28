

package net.sourceforge.pmd.ast;

public class ASTCastExpression extends SimpleJavaTypeNode {
    public ASTCastExpression(int id) {
        super(id);
    }

    public ASTCastExpression(JavaParser p, int id) {
        super(p, id);
    }


    
    public Object jjtAccept(JavaParserVisitor visitor, Object data) {
        return visitor.visit(this, data);
    }
}
