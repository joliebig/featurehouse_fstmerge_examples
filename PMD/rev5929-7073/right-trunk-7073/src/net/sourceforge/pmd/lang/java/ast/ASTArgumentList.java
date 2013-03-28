

package net.sourceforge.pmd.lang.java.ast;

public class ASTArgumentList extends AbstractJavaNode {
    public ASTArgumentList(int id) {
        super(id);
    }

    public ASTArgumentList(JavaParser p, int id) {
        super(p, id);
    }


    
    public Object jjtAccept(JavaParserVisitor visitor, Object data) {
        return visitor.visit(this, data);
    }
}
