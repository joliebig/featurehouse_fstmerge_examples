

package net.sourceforge.pmd.jsp.ast;

public class ASTCData extends SimpleNode {
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
