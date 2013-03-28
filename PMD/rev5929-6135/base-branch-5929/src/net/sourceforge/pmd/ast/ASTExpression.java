

package net.sourceforge.pmd.ast;

public class ASTExpression extends SimpleJavaTypeNode {
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
