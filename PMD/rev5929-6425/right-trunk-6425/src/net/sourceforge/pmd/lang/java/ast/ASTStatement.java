

package net.sourceforge.pmd.lang.java.ast;

public class ASTStatement extends AbstractJavaNode {
    public ASTStatement(int id) {
        super(id);
    }

    public ASTStatement(JavaParser p, int id) {
        super(p, id);
    }


    
    public Object jjtAccept(JavaParserVisitor visitor, Object data) {
        return visitor.visit(this, data);
    }
}
