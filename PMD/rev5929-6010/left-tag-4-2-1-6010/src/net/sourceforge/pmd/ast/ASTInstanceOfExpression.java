

package net.sourceforge.pmd.ast;

public class ASTInstanceOfExpression extends SimpleJavaTypeNode {
    public ASTInstanceOfExpression(int id) {
        super(id);
    }

    public ASTInstanceOfExpression(JavaParser p, int id) {
        super(p, id);
    }


    
    public Object jjtAccept(JavaParserVisitor visitor, Object data) {
        return visitor.visit(this, data);
    }
}
