

package net.sourceforge.pmd.lang.java.ast;

public class ASTImportDeclaration extends AbstractJavaTypeNode {

    private boolean isImportOnDemand;
    private boolean isStatic;

    public ASTImportDeclaration(int id) {
        super(id);
    }

    public ASTImportDeclaration(JavaParser p, int id) {
        super(p, id);
    }

    public void setImportOnDemand() {
        isImportOnDemand = true;
    }

    public boolean isImportOnDemand() {
        return isImportOnDemand;
    }

    public void setStatic() {
        isStatic = true;
    }

    public boolean isStatic() {
        return isStatic;
    }

    
    public ASTName getImportedNameNode() {
        return (ASTName) jjtGetChild(0);
    }

    public String getImportedName() {
        return ((ASTName) jjtGetChild(0)).getImage();
    }

    public String getPackageName() {
        String importName = getImportedName();
        if (isImportOnDemand) {
            return importName;
        }
        if (importName.indexOf('.') == -1) {
            return "";
        }
        int lastDot = importName.lastIndexOf('.');
        return importName.substring(0, lastDot);
    }

    
    public Object jjtAccept(JavaParserVisitor visitor, Object data) {
        return visitor.visit(this, data);
    }
    
    private Package pkg;
    public void setPackage(Package packge){
        this.pkg = packge;
    }
    
    public Package getPackage(){
        return this.pkg;
    }
}
