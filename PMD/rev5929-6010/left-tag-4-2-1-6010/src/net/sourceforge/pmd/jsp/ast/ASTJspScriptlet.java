

package net.sourceforge.pmd.jsp.ast;

public class ASTJspScriptlet extends SimpleNode {
    public ASTJspScriptlet(int id) {
        super(id);
    }

    public ASTJspScriptlet(JspParser p, int id) {
        super(p, id);
    }


    
    public Object jjtAccept(JspParserVisitor visitor, Object data) {
        return visitor.visit(this, data);
    }
}
