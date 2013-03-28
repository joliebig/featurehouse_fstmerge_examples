

package net.sourceforge.pmd.ast;

public class ASTMemberValuePairs extends SimpleJavaNode {
    public ASTMemberValuePairs(int id) {
        super(id);
    }

    public ASTMemberValuePairs(JavaParser p, int id) {
        super(p, id);
    }


    
    public Object jjtAccept(JavaParserVisitor visitor, Object data) {
        return visitor.visit(this, data);
    }
}
