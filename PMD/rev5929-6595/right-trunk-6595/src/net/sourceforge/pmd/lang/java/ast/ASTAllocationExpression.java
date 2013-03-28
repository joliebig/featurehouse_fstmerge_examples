

package net.sourceforge.pmd.lang.java.ast;

public class ASTAllocationExpression extends AbstractJavaTypeNode {
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
