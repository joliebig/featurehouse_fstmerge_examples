

package net.sourceforge.pmd.lang.java.ast;

public class ASTContinueStatement extends AbstractJavaNode {
    public ASTContinueStatement(int id) {
        super(id);
    }

    public ASTContinueStatement(JavaParser p, int id) {
        super(p, id);
    }


    
    public Object jjtAccept(JavaParserVisitor visitor, Object data) {
        return visitor.visit(this, data);
    }
}
