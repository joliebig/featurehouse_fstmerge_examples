

package net.sourceforge.pmd.lang.jsp.ast;

public class ASTJspDeclarations extends AbstractJspNode {
    public ASTJspDeclarations(int id) {
        super(id);
    }

    public ASTJspDeclarations(JspParser p, int id) {
        super(p, id);
    }


    
    public Object jjtAccept(JspParserVisitor visitor, Object data) {
        return visitor.visit(this, data);
    }
}
