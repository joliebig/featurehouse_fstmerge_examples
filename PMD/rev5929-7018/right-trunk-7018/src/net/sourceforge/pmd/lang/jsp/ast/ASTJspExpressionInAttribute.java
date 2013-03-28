

package net.sourceforge.pmd.lang.jsp.ast;

public class ASTJspExpressionInAttribute extends AbstractJspNode {
    public ASTJspExpressionInAttribute(int id) {
        super(id);
    }

    public ASTJspExpressionInAttribute(JspParser p, int id) {
        super(p, id);
    }


    
    public Object jjtAccept(JspParserVisitor visitor, Object data) {
        return visitor.visit(this, data);
    }
}
