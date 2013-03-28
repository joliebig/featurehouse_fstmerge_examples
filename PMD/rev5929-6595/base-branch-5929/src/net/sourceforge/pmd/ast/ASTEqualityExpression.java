

package net.sourceforge.pmd.ast;

public class ASTEqualityExpression extends SimpleJavaTypeNode {
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
