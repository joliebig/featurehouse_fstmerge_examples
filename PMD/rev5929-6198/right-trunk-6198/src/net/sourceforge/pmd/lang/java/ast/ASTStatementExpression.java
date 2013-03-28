

package net.sourceforge.pmd.lang.java.ast;

public class ASTStatementExpression extends AbstractJavaTypeNode {
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
