

package net.sourceforge.pmd.lang.jsp.ast;

public class ASTCommentTag extends AbstractJspNode {
    public ASTCommentTag(int id) {
        super(id);
    }

    public ASTCommentTag(JspParser p, int id) {
        super(p, id);
    }


    
    public Object jjtAccept(JspParserVisitor visitor, Object data) {
        return visitor.visit(this, data);
    }
}
