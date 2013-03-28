

package net.sourceforge.pmd.jsp.ast;

public class ASTAttribute extends SimpleNode {
    
    private String name;

    
    public String getName() {
        return name;
    }

    
    public void setName(String name) {
        this.name = name;
    }


    
    public boolean isHasNamespacePrefix() {
        return (name.indexOf(':') >= 0);
    }

    
    public String getNamespacePrefix() {
        int colonIndex = name.indexOf(':');
        return ((colonIndex >= 0)
                ? name.substring(0, colonIndex)
                : "");
    }

    
    public String getLocalName() {
        int colonIndex = name.indexOf(':');
        return ((colonIndex >= 0)
                ? name.substring(colonIndex + 1)
                : name);
    }

    
    public String toString(String prefix) {
        return super.toString(prefix) + " name=[" + name + "]";
    }




    public ASTAttribute(int id) {
        super(id);
    }

    public ASTAttribute(JspParser p, int id) {
        super(p, id);
    }


    
    public Object jjtAccept(JspParserVisitor visitor, Object data) {
        return visitor.visit(this, data);
    }
}
