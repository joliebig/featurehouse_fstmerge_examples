

package net.sourceforge.pmd.ast;

public class ASTMarkerAnnotation extends SimpleJavaTypeNode {
    public ASTMarkerAnnotation(int id) {
        super(id);
    }

    public ASTMarkerAnnotation(JavaParser p, int id) {
        super(p, id);
    }


    
    public Object jjtAccept(JavaParserVisitor visitor, Object data) {
        return visitor.visit(this, data);
    }
}
