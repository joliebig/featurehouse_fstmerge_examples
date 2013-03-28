

package net.sourceforge.pmd.lang.jsp.ast;

public class ASTElExpression extends AbstractJspNode {
    public ASTElExpression(int id) {
        super(id);
    }

    public ASTElExpression(JspParser p, int id) {
        super(p, id);
    }


    
    public Object jjtAccept(JspParserVisitor visitor, Object data) {
        return visitor.visit(this, data);
    }
}
