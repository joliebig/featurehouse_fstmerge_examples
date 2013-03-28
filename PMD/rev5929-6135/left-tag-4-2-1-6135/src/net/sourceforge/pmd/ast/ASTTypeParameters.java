

package net.sourceforge.pmd.ast;

public class ASTTypeParameters extends SimpleJavaNode {
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
