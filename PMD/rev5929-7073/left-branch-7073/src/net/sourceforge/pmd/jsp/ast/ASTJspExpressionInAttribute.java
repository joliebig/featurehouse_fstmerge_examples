

package net.sourceforge.pmd.jsp.ast;

public class ASTJspExpressionInAttribute extends SimpleNode {
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
