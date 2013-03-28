

package net.sourceforge.pmd.lang.jsp.ast;

public class ASTElement extends AbstractJspNode {



    
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
