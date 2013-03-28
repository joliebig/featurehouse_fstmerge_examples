

package net.sourceforge.pmd.lang.java.ast;

public class ASTTypeArgument extends AbstractJavaNode {
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
