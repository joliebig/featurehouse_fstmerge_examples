

package net.sourceforge.pmd.lang.jsp.ast;

public class ASTJspComment extends AbstractJspNode {
    public ASTJspComment(int id) {
        super(id);
    }

    public ASTJspComment(JspParser p, int id) {
        super(p, id);
    }


    
    public Object jjtAccept(JspParserVisitor visitor, Object data) {
        return visitor.visit(this, data);
    }
}
