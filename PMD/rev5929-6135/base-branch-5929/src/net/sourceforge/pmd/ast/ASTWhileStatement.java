

package net.sourceforge.pmd.ast;

public class ASTWhileStatement extends SimpleJavaNode {
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
