

package net.sourceforge.pmd.jsp.ast;

public class ASTJspDeclarations extends SimpleNode {
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
