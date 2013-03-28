

package net.sourceforge.pmd.ast;

public class ASTConditionalExpression extends SimpleJavaTypeNode {
    public ASTConditionalExpression(int id) {
        super(id);
    }

    public ASTConditionalExpression(JavaParser p, int id) {
        super(p, id);
    }

    private boolean isTernary;

    public void setTernary() {
        isTernary = true;
    }

    public boolean isTernary() {
        return this.isTernary;
    }

    
    public Object jjtAccept(JavaParserVisitor visitor, Object data) {
        return visitor.visit(this, data);
    }
}
