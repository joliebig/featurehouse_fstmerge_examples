

package net.sourceforge.pmd.lang.java.ast;

public class ASTNormalAnnotation extends AbstractJavaNode {
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
