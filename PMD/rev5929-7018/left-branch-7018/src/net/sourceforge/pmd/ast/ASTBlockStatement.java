

package net.sourceforge.pmd.ast;

public class ASTBlockStatement extends SimpleJavaNode {
    public ASTBlockStatement(int id) {
        super(id);
    }

    public ASTBlockStatement(JavaParser p, int id) {
        super(p, id);
    }


    
    public Object jjtAccept(JavaParserVisitor visitor, Object data) {
        return visitor.visit(this, data);
    }

    
    public final boolean isAllocation() {
        return !findChildrenOfType(ASTAllocationExpression.class).isEmpty();
    }
}
