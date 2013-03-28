

package net.sourceforge.pmd.lang.java.ast;

public class ASTSwitchLabel extends AbstractJavaNode {
    public ASTSwitchLabel(int id) {
        super(id);
    }

    public ASTSwitchLabel(JavaParser p, int id) {
        super(p, id);
    }

    private boolean isDefault;

    public void setDefault() {
        isDefault = true;
    }

    public boolean isDefault() {
        return isDefault;
    }


    
    public Object jjtAccept(JavaParserVisitor visitor, Object data) {
        return visitor.visit(this, data);
    }
}
