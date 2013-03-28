

package net.sourceforge.pmd.lang.java.ast;

public class ASTExtendsList extends AbstractJavaNode {
    public ASTExtendsList(int id) {
        super(id);
    }

    public ASTExtendsList(JavaParser p, int id) {
        super(p, id);
    }


    
    public Object jjtAccept(JavaParserVisitor visitor, Object data) {
        return visitor.visit(this, data);
    }
}
