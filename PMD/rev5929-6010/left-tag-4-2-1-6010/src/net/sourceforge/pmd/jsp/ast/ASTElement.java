

package net.sourceforge.pmd.jsp.ast;

public class ASTElement extends SimpleNode {



    
    private String name;

    
    private boolean empty; 


    
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

    
    public String getName() {
        return name;
    }

    
    public void setName(String name) {
        this.name = name;
    }

    
    public boolean isEmpty() {
        return empty;
    }

    
    public void setEmpty(boolean empty) {
        this.empty = empty;
    }

    
    public String toString(String prefix) {
        return super.toString(prefix) + " name=[" + name + "] ";
    }




    public ASTElement(int id) {
        super(id);
    }

    public ASTElement(JspParser p, int id) {
        super(p, id);
    }


    
    public Object jjtAccept(JspParserVisitor visitor, Object data) {
        return visitor.visit(this, data);
    }
}
