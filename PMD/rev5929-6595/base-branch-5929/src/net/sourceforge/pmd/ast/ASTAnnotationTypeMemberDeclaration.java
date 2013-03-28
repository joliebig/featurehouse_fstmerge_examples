

package net.sourceforge.pmd.ast;

public class ASTAnnotationTypeMemberDeclaration extends SimpleJavaNode {
    public ASTAnnotationTypeMemberDeclaration(int id) {
        super(id);
    }

    public ASTAnnotationTypeMemberDeclaration(JavaParser p, int id) {
        super(p, id);
    }


    
    public Object jjtAccept(JavaParserVisitor visitor, Object data) {
        return visitor.visit(this, data);
    }
}
