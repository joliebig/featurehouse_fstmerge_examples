

package net.sourceforge.pmd.ast;

public class ASTMemberValueArrayInitializer extends SimpleJavaNode {
    public ASTMemberValueArrayInitializer(int id) {
        super(id);
    }

    public ASTMemberValueArrayInitializer(JavaParser p, int id) {
        super(p, id);
    }


    
    public Object jjtAccept(JavaParserVisitor visitor, Object data) {
        return visitor.visit(this, data);
    }
}
