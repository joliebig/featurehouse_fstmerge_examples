

package net.sourceforge.pmd.ast;

public class ASTAnnotationTypeDeclaration extends SimpleJavaAccessTypeNode {
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
