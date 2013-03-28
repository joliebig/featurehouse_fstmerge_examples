

package net.sourceforge.pmd.lang.jsp.ast;

public class ASTCData extends AbstractJspNode {
    public ASTCData(int id) {
        super(id);
    }

    public ASTCData(JspParser p, int id) {
        super(p, id);
    }


    
    public Object jjtAccept(JspParserVisitor visitor, Object data) {
        return visitor.visit(this, data);
    }
}
