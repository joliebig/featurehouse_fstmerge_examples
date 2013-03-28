

package net.sourceforge.pmd.jsp.ast;

public class ASTDoctypeDeclaration extends SimpleNode {

    

    
    private String name;

    
    public String getName() {
        return name;
    }

    
    public void setName(String name) {
        this.name = name;
    }

    
    public String toString(String prefix) {
        return super.toString(prefix) + " name=[" + name + "] ";
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
