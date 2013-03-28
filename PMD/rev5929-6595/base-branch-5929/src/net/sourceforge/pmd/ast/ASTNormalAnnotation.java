

package net.sourceforge.pmd.ast;

public class ASTNormalAnnotation extends SimpleJavaNode {
    public ASTNormalAnnotation(int id) {
        super(id);
    }

    public ASTNormalAnnotation(JavaParser p, int id) {
        super(p, id);
    }


    
    public Object jjtAccept(JavaParserVisitor visitor, Object data) {
        return visitor.visit(this, data);
    }
}
