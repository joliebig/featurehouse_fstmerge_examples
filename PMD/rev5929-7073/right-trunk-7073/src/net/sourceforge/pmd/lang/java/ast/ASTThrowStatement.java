

package net.sourceforge.pmd.lang.java.ast;

public class ASTThrowStatement extends AbstractJavaNode {
    public ASTThrowStatement(int id) {
        super(id);
    }

    public ASTThrowStatement(JavaParser p, int id) {
        super(p, id);
    }


    
    @Override
    public Object jjtAccept(JavaParserVisitor visitor, Object data) {
        return visitor.visit(this, data);
    }

    
    public final String getFirstClassOrInterfaceTypeImage() {
        final ASTClassOrInterfaceType t = getFirstDescendantOfType(ASTClassOrInterfaceType.class);
        return t == null ? null : t.getImage();
    }
}
