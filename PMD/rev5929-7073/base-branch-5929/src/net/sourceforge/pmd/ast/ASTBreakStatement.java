

package net.sourceforge.pmd.ast;

public class ASTBreakStatement extends SimpleJavaNode {
    public ASTBreakStatement(int id) {
        super(id);
    }

    public ASTBreakStatement(JavaParser p, int id) {
        super(p, id);
    }


    
    public Object jjtAccept(JavaParserVisitor visitor, Object data) {
        return visitor.visit(this, data);
    }
}
