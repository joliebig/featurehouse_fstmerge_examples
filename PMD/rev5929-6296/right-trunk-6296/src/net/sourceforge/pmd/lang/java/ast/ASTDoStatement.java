

package net.sourceforge.pmd.lang.java.ast;

public class ASTDoStatement extends AbstractJavaNode {
    public ASTDoStatement(int id) {
        super(id);
    }

    public ASTDoStatement(JavaParser p, int id) {
        super(p, id);
    }


    
    public Object jjtAccept(JavaParserVisitor visitor, Object data) {
        return visitor.visit(this, data);
    }
}
