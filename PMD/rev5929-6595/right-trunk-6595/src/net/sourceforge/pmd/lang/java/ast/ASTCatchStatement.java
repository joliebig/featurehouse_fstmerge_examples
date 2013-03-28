

package net.sourceforge.pmd.lang.java.ast;

public class ASTCatchStatement extends AbstractJavaNode {
    public ASTCatchStatement(int id) {
        super(id);
    }

    public ASTCatchStatement(JavaParser p, int id) {
        super(p, id);
    }


    
    public Object jjtAccept(JavaParserVisitor visitor, Object data) {
        return visitor.visit(this, data);
    }
}
