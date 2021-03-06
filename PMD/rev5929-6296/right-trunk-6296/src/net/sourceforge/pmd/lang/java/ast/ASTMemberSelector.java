

package net.sourceforge.pmd.lang.java.ast;

public class ASTMemberSelector extends AbstractJavaNode {
    public ASTMemberSelector(int id) {
        super(id);
    }

    public ASTMemberSelector(JavaParser p, int id) {
        super(p, id);
    }

    
    public Object jjtAccept(JavaParserVisitor visitor, Object data) {
        return visitor.visit(this, data);
    }
}
