

package net.sourceforge.pmd.lang.jsp.ast;

public class ASTText extends AbstractJspNode {
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
