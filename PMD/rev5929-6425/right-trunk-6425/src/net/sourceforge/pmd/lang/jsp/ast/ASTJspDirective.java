

package net.sourceforge.pmd.lang.jsp.ast;

public class ASTJspDirective extends AbstractJspNode {

    

    
    private String name;

    
    public String getName() {
        return name;
    }

    
    public void setName(String name) {
        this.name = name;
    }


    public ASTJspDirective(int id) {
        super(id);
    }

    public ASTJspDirective(JspParser p, int id) {
        super(p, id);
    }


    
    public Object jjtAccept(JspParserVisitor visitor, Object data) {
        return visitor.visit(this, data);
    }
}
