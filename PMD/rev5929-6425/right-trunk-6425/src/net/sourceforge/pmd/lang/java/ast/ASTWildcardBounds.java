

package net.sourceforge.pmd.lang.java.ast;

public class ASTWildcardBounds extends AbstractJavaNode {
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
