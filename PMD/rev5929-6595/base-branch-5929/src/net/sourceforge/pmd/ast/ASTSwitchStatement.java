

package net.sourceforge.pmd.ast;

public class ASTSwitchStatement extends SimpleJavaNode {
    public ASTSwitchStatement(int id) {
        super(id);
    }

    public ASTSwitchStatement(JavaParser p, int id) {
        super(p, id);
    }


    
    public Object jjtAccept(JavaParserVisitor visitor, Object data) {
        return visitor.visit(this, data);
    }
}
