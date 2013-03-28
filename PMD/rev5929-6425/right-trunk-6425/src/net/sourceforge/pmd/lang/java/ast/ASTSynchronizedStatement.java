

package net.sourceforge.pmd.lang.java.ast;

public class ASTSynchronizedStatement extends AbstractJavaNode {
    public ASTSynchronizedStatement(int id) {
        super(id);
    }

    public ASTSynchronizedStatement(JavaParser p, int id) {
        super(p, id);
    }


    
    public Object jjtAccept(JavaParserVisitor visitor, Object data) {
        return visitor.visit(this, data);
    }
}
