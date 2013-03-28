

package net.sourceforge.pmd.lang.java.ast;

public class ASTClassOrInterfaceDeclaration extends AbstractJavaAccessTypeNode {
    public ASTClassOrInterfaceDeclaration(int id) {
        super(id);
    }

    public ASTClassOrInterfaceDeclaration(JavaParser p, int id) {
        super(p, id);
    }
    
    @Override
    public boolean isFindBoundary() {
	return isNested();
    }

    
    public Object jjtAccept(JavaParserVisitor visitor, Object data) {
        return visitor.visit(this, data);
    }

    public boolean isNested() {
        return jjtGetParent() instanceof ASTClassOrInterfaceBodyDeclaration;
    }

    private boolean isInterface;

    public boolean isInterface() {
        return this.isInterface;
    }

    public void setInterface() {
        this.isInterface = true;
    }
}
