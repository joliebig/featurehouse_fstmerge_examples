

package net.sourceforge.pmd.ast;

public class ASTRelationalExpression extends SimpleJavaTypeNode {
    public ASTRelationalExpression(int id) {
        super(id);
    }

    public ASTRelationalExpression(JavaParser p, int id) {
        super(p, id);
    }


    
    public Object jjtAccept(JavaParserVisitor visitor, Object data) {
        return visitor.visit(this, data);
    }
}
