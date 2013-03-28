

package net.sourceforge.pmd.lang.java.ast;

public class ASTNullLiteral extends AbstractJavaTypeNode {
    public ASTNullLiteral(int id) {
        super(id);
    }

    public ASTNullLiteral(JavaParser p, int id) {
        super(p, id);
    }


    
    public Object jjtAccept(JavaParserVisitor visitor, Object data) {
        return visitor.visit(this, data);
    }
}
