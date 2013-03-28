

package net.sourceforge.pmd.jsp.ast;

public class ASTJspDeclaration extends SimpleNode {
    public ASTJspDeclaration(int id) {
        super(id);
    }

    public ASTJspDeclaration(JspParser p, int id) {
        super(p, id);
    }


    
    public Object jjtAccept(JspParserVisitor visitor, Object data) {
        return visitor.visit(this, data);
    }
}
