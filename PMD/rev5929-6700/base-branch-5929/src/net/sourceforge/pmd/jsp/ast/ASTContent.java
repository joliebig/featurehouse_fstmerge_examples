

package net.sourceforge.pmd.jsp.ast;

public class ASTContent extends SimpleNode {
    public ASTContent(int id) {
        super(id);
    }

    public ASTContent(JspParser p, int id) {
        super(p, id);
    }


    
    public Object jjtAccept(JspParserVisitor visitor, Object data) {
        return visitor.visit(this, data);
    }
}
