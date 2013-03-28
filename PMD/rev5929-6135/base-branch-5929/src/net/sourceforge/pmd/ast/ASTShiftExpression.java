

package net.sourceforge.pmd.ast;

public class ASTShiftExpression extends SimpleJavaTypeNode {
    public ASTShiftExpression(int id) {
        super(id);
    }

    public ASTShiftExpression(JavaParser p, int id) {
        super(p, id);
    }


    
    public Object jjtAccept(JavaParserVisitor visitor, Object data) {
        return visitor.visit(this, data);
    }
}
