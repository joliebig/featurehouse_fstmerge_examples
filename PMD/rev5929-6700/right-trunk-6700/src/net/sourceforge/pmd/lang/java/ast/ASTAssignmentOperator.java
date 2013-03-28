

package net.sourceforge.pmd.lang.java.ast;

public class ASTAssignmentOperator extends AbstractJavaNode {
    public ASTAssignmentOperator(int id) {
        super(id);
    }

    public ASTAssignmentOperator(JavaParser p, int id) {
        super(p, id);
    }

    private boolean isCompound;

    public void setCompound() {
        isCompound = true;
    }

    public boolean isCompound() {
        return this.isCompound;
    }


    
    public Object jjtAccept(JavaParserVisitor visitor, Object data) {
        return visitor.visit(this, data);
    }
}
