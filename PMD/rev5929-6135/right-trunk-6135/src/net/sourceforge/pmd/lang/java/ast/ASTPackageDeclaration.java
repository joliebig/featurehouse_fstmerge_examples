

package net.sourceforge.pmd.lang.java.ast;

public class ASTPackageDeclaration extends AbstractJavaNode {
    public ASTPackageDeclaration(int id) {
	super(id);
    }

    public ASTPackageDeclaration(JavaParser p, int id) {
	super(p, id);
    }

    
    @Override
    public Object jjtAccept(JavaParserVisitor visitor, Object data) {
	return visitor.visit(this, data);
    }

    public String getPackageNameImage() {
	return ((ASTName) jjtGetChild(this.jjtGetNumChildren() - 1)).getImage();
    }
}
