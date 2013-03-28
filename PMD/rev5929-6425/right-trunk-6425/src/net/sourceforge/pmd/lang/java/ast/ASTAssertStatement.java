

package net.sourceforge.pmd.lang.java.ast;

public class ASTAssertStatement extends AbstractJavaNode {
    public ASTAssertStatement(int id) {
        super(id);
    }

    public ASTAssertStatement(JavaParser p, int id) {
        super(p, id);
    }


    
    public Object jjtAccept(JavaParserVisitor visitor, Object data) {
        return visitor.visit(this, data);
    }
}
