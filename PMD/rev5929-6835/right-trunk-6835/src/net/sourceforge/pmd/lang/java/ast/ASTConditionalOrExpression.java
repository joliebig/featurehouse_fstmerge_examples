

package net.sourceforge.pmd.lang.java.ast;

public class ASTConditionalOrExpression extends AbstractJavaTypeNode {
    public ASTConditionalOrExpression(int id) {
        super(id);
    }

    public ASTConditionalOrExpression(JavaParser p, int id) {
        super(p, id);
    }


    
    public Object jjtAccept(JavaParserVisitor visitor, Object data) {
        return visitor.visit(this, data);
    }
}
