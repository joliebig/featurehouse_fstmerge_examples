

package net.sourceforge.pmd.lang.java.ast;

public class ASTStatementExpressionList extends AbstractJavaNode {
    public ASTStatementExpressionList(int id) {
        super(id);
    }

    public ASTStatementExpressionList(JavaParser p, int id) {
        super(p, id);
    }


    
    public Object jjtAccept(JavaParserVisitor visitor, Object data) {
        return visitor.visit(this, data);
    }
}
