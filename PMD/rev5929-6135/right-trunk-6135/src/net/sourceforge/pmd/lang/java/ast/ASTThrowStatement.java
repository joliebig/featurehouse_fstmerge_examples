

package net.sourceforge.pmd.lang.java.ast;

public class ASTThrowStatement extends AbstractJavaNode {
    public ASTThrowStatement(int id) {
        super(id);
    }

    public ASTThrowStatement(JavaParser p, int id) {
        super(p, id);
    }


    
    public Object jjtAccept(JavaParserVisitor visitor, Object data) {
        return visitor.visit(this, data);
    }

    
    public final String getFirstClassOrInterfaceTypeImage() {
        final ASTClassOrInterfaceType t = getFirstChildOfType(ASTClassOrInterfaceType.class);
        return t == null ? null : t.getImage();
    }
}
