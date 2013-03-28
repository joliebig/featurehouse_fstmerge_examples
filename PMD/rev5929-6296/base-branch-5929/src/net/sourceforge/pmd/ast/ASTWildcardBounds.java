

package net.sourceforge.pmd.ast;

public class ASTWildcardBounds extends SimpleJavaNode {
    public ASTWildcardBounds(int id) {
        super(id);
    }

    public ASTWildcardBounds(JavaParser p, int id) {
        super(p, id);
    }


    
    public Object jjtAccept(JavaParserVisitor visitor, Object data) {
        return visitor.visit(this, data);
    }
}
