

package net.sourceforge.pmd.lang.jsp.ast;

public class ASTHtmlScript extends AbstractJspNode {
    public ASTHtmlScript(int id) {
	super(id);
    }

    public ASTHtmlScript(JspParser p, int id) {
	super(p, id);
    }

    
    @Override
    public Object jjtAccept(JspParserVisitor visitor, Object data) {
	return visitor.visit(this, data);
    }
}
