

package net.sourceforge.pmd.ast;

public class ASTImplementsList extends SimpleJavaNode {
    public ASTImplementsList(int id) {
        super(id);
    }

    public ASTImplementsList(JavaParser p, int id) {
        super(p, id);
    }


    
    public Object jjtAccept(JavaParserVisitor visitor, Object data) {
        return visitor.visit(this, data);
    }
}
