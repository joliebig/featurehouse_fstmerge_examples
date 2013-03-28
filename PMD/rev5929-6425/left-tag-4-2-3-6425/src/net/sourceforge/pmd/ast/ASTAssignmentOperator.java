

package net.sourceforge.pmd.ast;

public class ASTAssignmentOperator extends SimpleJavaNode {
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

    public void dump(String prefix) {
        System.out.println(toString(prefix) + ":" + getImage() + (isCompound ? "(compound)" : "(simple)"));
        dumpChildren(prefix);
    }
}
