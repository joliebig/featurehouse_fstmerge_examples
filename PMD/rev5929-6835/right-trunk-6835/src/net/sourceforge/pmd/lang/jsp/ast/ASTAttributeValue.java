

package net.sourceforge.pmd.lang.jsp.ast;

public class ASTAttributeValue extends AbstractJspNode {
    public ASTAttributeValue(int id) {
        super(id);
    }

    public ASTAttributeValue(JspParser p, int id) {
        super(p, id);
    }


    
    public Object jjtAccept(JspParserVisitor visitor, Object data) {
        return visitor.visit(this, data);
    }
}
