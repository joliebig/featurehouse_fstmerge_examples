

package net.sourceforge.pmd.lang.java.ast;

public class ASTAnnotationTypeDeclaration extends AbstractJavaAccessTypeNode {
    public ASTAnnotationTypeDeclaration(int id) {
        super(id);
    }

    public ASTAnnotationTypeDeclaration(JavaParser p, int id) {
        super(p, id);
    }


    
    public Object jjtAccept(JavaParserVisitor visitor, Object data) {
        return visitor.visit(this, data);
    }

}
