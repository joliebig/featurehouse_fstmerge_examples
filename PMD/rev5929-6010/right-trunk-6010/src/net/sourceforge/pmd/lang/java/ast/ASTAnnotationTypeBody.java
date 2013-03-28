

package net.sourceforge.pmd.lang.java.ast;

public class ASTAnnotationTypeBody extends AbstractJavaNode {
    public ASTAnnotationTypeBody(int id) {
        super(id);
    }

    public ASTAnnotationTypeBody(JavaParser p, int id) {
        super(p, id);
    }


    
    public Object jjtAccept(JavaParserVisitor visitor, Object data) {
        return visitor.visit(this, data);
    }
}
