

package net.sourceforge.pmd.jsp.ast;

public class ASTText extends SimpleNode {
    public ASTText(int id) {
        super(id);
    }

    public ASTText(JspParser p, int id) {
        super(p, id);
    }


    
    public Object jjtAccept(JspParserVisitor visitor, Object data) {
        return visitor.visit(this, data);
    }
}
