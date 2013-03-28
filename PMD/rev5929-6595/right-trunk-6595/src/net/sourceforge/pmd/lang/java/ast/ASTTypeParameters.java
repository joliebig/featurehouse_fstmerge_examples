

package net.sourceforge.pmd.lang.java.ast;

public class ASTTypeParameters extends AbstractJavaNode {
    public ASTTypeParameters(int id) {
        super(id);
    }

    public ASTTypeParameters(JavaParser p, int id) {
        super(p, id);
    }


    
    public Object jjtAccept(JavaParserVisitor visitor, Object data) {
        return visitor.visit(this, data);
    }
}
