

package net.sourceforge.pmd.lang.java.ast;

public class ASTArguments extends AbstractJavaNode {
    public ASTArguments(int id) {
        super(id);
    }

    public ASTArguments(JavaParser p, int id) {
        super(p, id);
    }


    public int getArgumentCount() {
        if (this.jjtGetNumChildren() == 0) {
            return 0;
        }
        return this.jjtGetChild(0).jjtGetNumChildren();
    }

    
    public Object jjtAccept(JavaParserVisitor visitor, Object data) {
        return visitor.visit(this, data);
    }
}
