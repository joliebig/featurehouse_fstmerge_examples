

package net.sourceforge.pmd.ast;

public class ASTTypeParameter extends SimpleJavaNode {
    public ASTTypeParameter(int id) {
        super(id);
    }

    public ASTTypeParameter(JavaParser p, int id) {
        super(p, id);
    }


    
    public Object jjtAccept(JavaParserVisitor visitor, Object data) {
        return visitor.visit(this, data);
    }
}
