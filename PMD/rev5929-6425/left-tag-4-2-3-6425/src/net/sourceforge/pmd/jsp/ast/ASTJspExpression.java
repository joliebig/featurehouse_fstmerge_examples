

package net.sourceforge.pmd.jsp.ast;

public class ASTJspExpression extends SimpleNode {
    public ASTJspExpression(int id) {
        super(id);
    }

    public ASTJspExpression(JspParser p, int id) {
        super(p, id);
    }


    
    public Object jjtAccept(JspParserVisitor visitor, Object data) {
        return visitor.visit(this, data);
    }
}
