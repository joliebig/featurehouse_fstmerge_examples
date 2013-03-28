

package net.sourceforge.pmd.ast;

public class ASTNameList extends SimpleJavaNode {
    public ASTNameList(int id) {
        super(id);
    }

    public ASTNameList(JavaParser p, int id) {
        super(p, id);
    }


    
    public Object jjtAccept(JavaParserVisitor visitor, Object data) {
        return visitor.visit(this, data);
    }
}
