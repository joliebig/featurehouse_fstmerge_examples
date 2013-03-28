

package net.sourceforge.pmd.ast;

public class ASTForUpdate extends SimpleJavaNode {
    public ASTForUpdate(int id) {
        super(id);
    }

    public ASTForUpdate(JavaParser p, int id) {
        super(p, id);
    }


    
    public Object jjtAccept(JavaParserVisitor visitor, Object data) {
        return visitor.visit(this, data);
    }
}
