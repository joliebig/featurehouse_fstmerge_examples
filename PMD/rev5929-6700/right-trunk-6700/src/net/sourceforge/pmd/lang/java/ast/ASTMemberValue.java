

package net.sourceforge.pmd.lang.java.ast;

public class ASTMemberValue extends AbstractJavaNode {
    public ASTMemberValue(int id) {
        super(id);
    }

    public ASTMemberValue(JavaParser p, int id) {
        super(p, id);
    }


    
    public Object jjtAccept(JavaParserVisitor visitor, Object data) {
        return visitor.visit(this, data);
    }
}
