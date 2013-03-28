

package net.sourceforge.pmd.ast;

public class ASTArrayDimsAndInits extends SimpleJavaNode {
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
