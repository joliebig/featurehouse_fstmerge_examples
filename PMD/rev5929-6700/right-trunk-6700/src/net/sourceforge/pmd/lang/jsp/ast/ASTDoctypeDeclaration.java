

package net.sourceforge.pmd.lang.jsp.ast;

public class ASTDoctypeDeclaration extends AbstractJspNode {

    

    
    private String name;

    
    public String getName() {
        return name;
    }

    
    public void setName(String name) {
        this.name = name;
    }


    public ASTDoctypeDeclaration(int id) {
        super(id);
    }

    public ASTDoctypeDeclaration(JspParser p, int id) {
        super(p, id);
    }


    
    public Object jjtAccept(JspParserVisitor visitor, Object data) {
        return visitor.visit(this, data);
    }
}
