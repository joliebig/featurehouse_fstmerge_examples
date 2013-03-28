

package net.sourceforge.pmd.jsp.ast;

public class ASTValueBinding extends SimpleNode {
    public ASTValueBinding(int id) {
        super(id);
    }

    public ASTValueBinding(JspParser p, int id) {
        super(p, id);
    }


    
    public Object jjtAccept(JspParserVisitor visitor, Object data) {
        return visitor.visit(this, data);
    }
}
