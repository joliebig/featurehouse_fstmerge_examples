

package net.sourceforge.pmd.ast;

public class ASTExtendsList extends SimpleJavaNode {
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
