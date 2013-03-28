

package net.sourceforge.pmd.ast;

public class ASTStatementExpression extends SimpleJavaTypeNode {
    public ASTStatementExpression(int id) {
        super(id);
    }

    public ASTStatementExpression(JavaParser p, int id) {
        super(p, id);
    }


    
    public Object jjtAccept(JavaParserVisitor visitor, Object data) {
        return visitor.visit(this, data);
    }
}
