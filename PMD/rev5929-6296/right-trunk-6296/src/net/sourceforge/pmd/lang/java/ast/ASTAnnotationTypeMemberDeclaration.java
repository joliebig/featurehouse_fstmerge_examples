

package net.sourceforge.pmd.lang.java.ast;

public class ASTAnnotationTypeMemberDeclaration extends AbstractJavaNode {
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
