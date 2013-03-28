

package net.sourceforge.pmd.lang.jsp.ast;

public class ASTJspDocument extends AbstractJspNode {
    public ASTJspDocument(int id) {
        super(id);
    }

    public ASTJspDocument(JspParser p, int id) {
        super(p, id);
    }


    
    public Object jjtAccept(JspParserVisitor visitor, Object data) {
        return visitor.visit(this, data);
    }
}
