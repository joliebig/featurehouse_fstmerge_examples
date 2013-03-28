

package net.sourceforge.pmd.ast;

public class ASTTypeArgument extends SimpleJavaNode {
    public ASTTypeArgument(int id) {
        super(id);
    }

    public ASTTypeArgument(JavaParser p, int id) {
        super(p, id);
    }


    
    public Object jjtAccept(JavaParserVisitor visitor, Object data) {
        return visitor.visit(this, data);
    }
}
