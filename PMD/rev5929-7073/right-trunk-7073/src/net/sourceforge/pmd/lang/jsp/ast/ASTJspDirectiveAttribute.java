

package net.sourceforge.pmd.lang.jsp.ast;

public class ASTJspDirectiveAttribute extends AbstractJspNode {

    
    private String name;
    private String value;

    
    public String getName() {
        return name;
    }

    
    public void setName(String name) {
        this.name = name;
    }

    
    public String getValue() {
        return value;
    }

    
    public void setValue(String value) {
        this.value = value;
    }


    public ASTJspDirectiveAttribute(int id) {
        super(id);
    }

    public ASTJspDirectiveAttribute(JspParser p, int id) {
        super(p, id);
    }


    
    public Object jjtAccept(JspParserVisitor visitor, Object data) {
        return visitor.visit(this, data);
    }
}
