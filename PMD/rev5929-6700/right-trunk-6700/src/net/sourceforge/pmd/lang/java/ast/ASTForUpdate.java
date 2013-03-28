

package net.sourceforge.pmd.lang.java.ast;

public class ASTForUpdate extends AbstractJavaNode {
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
