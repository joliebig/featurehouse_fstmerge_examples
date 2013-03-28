

package net.sourceforge.pmd.ast;

public class ASTStatement extends SimpleJavaNode {
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
