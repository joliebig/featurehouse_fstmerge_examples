

package net.sourceforge.pmd.lang.jsp.ast;

public class ASTUnparsedText extends AbstractJspNode {
    public ASTUnparsedText(int id) {
        super(id);
    }

    public ASTUnparsedText(JspParser p, int id) {
        super(p, id);
    }


    
    public Object jjtAccept(JspParserVisitor visitor, Object data) {
        return visitor.visit(this, data);
    }
}
