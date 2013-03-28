

package net.sourceforge.pmd.ast;

public class ASTForStatement extends SimpleJavaNode {
    public ASTForStatement(int id) {
        super(id);
    }

    public ASTForStatement(JavaParser p, int id) {
        super(p, id);
    }


    
    public Object jjtAccept(JavaParserVisitor visitor, Object data) {
        return visitor.visit(this, data);
    }
}
