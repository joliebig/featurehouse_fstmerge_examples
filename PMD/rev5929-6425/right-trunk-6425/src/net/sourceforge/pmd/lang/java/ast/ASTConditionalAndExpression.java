

package net.sourceforge.pmd.lang.java.ast;

public class ASTConditionalAndExpression extends AbstractJavaTypeNode {
    public ASTConditionalAndExpression(int id) {
        super(id);
    }

    public ASTConditionalAndExpression(JavaParser p, int id) {
        super(p, id);
    }


    
    public Object jjtAccept(JavaParserVisitor visitor, Object data) {
        return visitor.visit(this, data);
    }
}
