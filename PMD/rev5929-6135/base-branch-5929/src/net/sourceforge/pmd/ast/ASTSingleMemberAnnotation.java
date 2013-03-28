

package net.sourceforge.pmd.ast;

public class ASTSingleMemberAnnotation extends SimpleJavaNode {
    public ASTSingleMemberAnnotation(int id) {
        super(id);
    }

    public ASTSingleMemberAnnotation(JavaParser p, int id) {
        super(p, id);
    }

    
    public Object jjtAccept(JavaParserVisitor visitor, Object data) {
        return visitor.visit(this, data);
    }
}
