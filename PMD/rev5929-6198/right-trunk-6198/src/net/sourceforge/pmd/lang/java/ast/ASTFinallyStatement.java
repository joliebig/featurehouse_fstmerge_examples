

package net.sourceforge.pmd.lang.java.ast;

public class ASTFinallyStatement extends AbstractJavaNode {
    public ASTFinallyStatement(int id) {
        super(id);
    }

    public ASTFinallyStatement(JavaParser p, int id) {
        super(p, id);
    }


    
    public Object jjtAccept(JavaParserVisitor visitor, Object data) {
        return visitor.visit(this, data);
    }
}
