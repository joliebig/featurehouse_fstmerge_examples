

package net.sourceforge.pmd.lang.jsp.ast;

public class ASTDoctypeExternalId extends AbstractJspNode {



    
    private String uri;

    
    private String publicId;

    public boolean isHasPublicId() {
        return (null != publicId);
    }

    
    public String getUri() {
        return uri;
    }

    
    public void setUri(String name) {
        this.uri = name;
    }

    
    public String getPublicId() {
        return (null == publicId ? "" : publicId);
    }

    
    public void setPublicId(String publicId) {
        this.publicId = publicId;
    }



    public ASTDoctypeExternalId(int id) {
        super(id);
    }

    public ASTDoctypeExternalId(JspParser p, int id) {
        super(p, id);
    }


    
    public Object jjtAccept(JspParserVisitor visitor, Object data) {
        return visitor.visit(this, data);
    }
}
