

package net.sourceforge.pmd.ast;

public class ASTPackageDeclaration extends SimpleJavaNode {
    public ASTPackageDeclaration(int id) {
        super(id);
    }

    public ASTPackageDeclaration(JavaParser p, int id) {
        super(p, id);
    }


    
    public Object jjtAccept(JavaParserVisitor visitor, Object data) {
        return visitor.visit(this, data);
    }

    public String getPackageNameImage() {
        return ((ASTName)jjtGetChild(0)).getImage();
    }
}
