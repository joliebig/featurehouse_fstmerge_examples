

package net.sourceforge.pmd.lang.jsp.ast;

public class ASTJspExpression extends AbstractJspNode {
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
