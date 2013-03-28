

package net.sourceforge.pmd.ast;

public class ASTPreIncrementExpression extends SimpleJavaTypeNode {
    public ASTPreIncrementExpression(int id) {
        super(id);
    }

    public ASTPreIncrementExpression(JavaParser p, int id) {
        super(p, id);
    }

    
    public Object jjtAccept(JavaParserVisitor visitor, Object data) {
        return visitor.visit(this, data);
    }

}
