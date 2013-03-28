

package net.sourceforge.pmd.jsp.ast;

public class ASTDoctypeExternalId extends SimpleNode {



    
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

    
    public String toString(String prefix) {
        return
                super.toString(prefix)
                + " uri=[" + uri + "] "
                + (null == publicId ? "" : "publicId=[" + publicId + "] ");
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
