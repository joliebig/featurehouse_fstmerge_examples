

package net.sourceforge.pmd.jsp.ast;

public class ASTDeclaration extends SimpleNode {


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



    public ASTDeclaration(int id) {
        super(id);
    }

    public ASTDeclaration(JspParser p, int id) {
        super(p, id);
    }


    
    public Object jjtAccept(JspParserVisitor visitor, Object data) {
        return visitor.visit(this, data);
    }
}
