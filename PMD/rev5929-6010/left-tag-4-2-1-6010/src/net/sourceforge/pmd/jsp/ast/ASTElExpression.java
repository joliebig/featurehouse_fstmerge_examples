

package net.sourceforge.pmd.jsp.ast;

public class ASTElExpression extends SimpleNode {
    public ASTElExpression(int id) {
        super(id);
    }

    public ASTElExpression(JspParser p, int id) {
        super(p, id);
    }


    
    public Object jjtAccept(JspParserVisitor visitor, Object data) {
        return visitor.visit(this, data);
    }
}
