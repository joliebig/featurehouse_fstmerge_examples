

package net.sourceforge.pmd.ast;

public class ASTInclusiveOrExpression extends SimpleJavaTypeNode {
    public ASTInclusiveOrExpression(int id) {
        super(id);
    }

    public ASTInclusiveOrExpression(JavaParser p, int id) {
        super(p, id);
    }


    
    public Object jjtAccept(JavaParserVisitor visitor, Object data) {
        return visitor.visit(this, data);
    }
}
