

package net.sourceforge.pmd.ast;

public class ASTAllocationExpression extends SimpleJavaTypeNode {
    public ASTAllocationExpression(int id) {
        super(id);
    }

    public ASTAllocationExpression(JavaParser p, int id) {
        super(p, id);
    }


    
    public Object jjtAccept(JavaParserVisitor visitor, Object data) {
        return visitor.visit(this, data);
    }
}
