

package net.sourceforge.pmd.lang.java.ast;

public class ASTWhileStatement extends AbstractJavaNode {
    public ASTWhileStatement(int id) {
        super(id);
    }

    public ASTWhileStatement(JavaParser p, int id) {
        super(p, id);
    }


    
    public Object jjtAccept(JavaParserVisitor visitor, Object data) {
        return visitor.visit(this, data);
    }
}
