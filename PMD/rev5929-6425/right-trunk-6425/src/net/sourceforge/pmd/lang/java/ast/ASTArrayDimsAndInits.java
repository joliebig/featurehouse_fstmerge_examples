

package net.sourceforge.pmd.lang.java.ast;

public class ASTArrayDimsAndInits extends AbstractJavaNode {
    public ASTArrayDimsAndInits(int id) {
        super(id);
    }

    public ASTArrayDimsAndInits(JavaParser p, int id) {
        super(p, id);
    }


    
    public Object jjtAccept(JavaParserVisitor visitor, Object data) {
        return visitor.visit(this, data);
    }
}
