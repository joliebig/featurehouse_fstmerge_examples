

package net.sourceforge.pmd.lang.java.ast;

public class ASTInitializer extends AbstractJavaNode {
    public ASTInitializer(int id) {
        super(id);
    }

    public ASTInitializer(JavaParser p, int id) {
        super(p, id);
    }


    
    public Object jjtAccept(JavaParserVisitor visitor, Object data) {
        return visitor.visit(this, data);
    }

    private boolean isStatic;

    public boolean isStatic() {
        return isStatic;
    }

    public void setStatic() {
        isStatic = true;
    }
}
