

package net.sourceforge.pmd.lang.java.ast;

public class ASTReturnStatement extends AbstractJavaNode {
    public ASTReturnStatement(int id) {
        super(id);
    }

    public ASTReturnStatement(JavaParser p, int id) {
        super(p, id);
    }


    
    public Object jjtAccept(JavaParserVisitor visitor, Object data) {
        return visitor.visit(this, data);
    }
}
