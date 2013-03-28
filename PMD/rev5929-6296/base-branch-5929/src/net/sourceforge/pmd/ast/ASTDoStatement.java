

package net.sourceforge.pmd.ast;

public class ASTDoStatement extends SimpleJavaNode {
    public ASTDoStatement(int id) {
        super(id);
    }

    public ASTDoStatement(JavaParser p, int id) {
        super(p, id);
    }


    
    public Object jjtAccept(JavaParserVisitor visitor, Object data) {
        return visitor.visit(this, data);
    }
}
