

package net.sourceforge.pmd.ast;

public class ASTMemberValuePair extends SimpleJavaNode {
    public ASTMemberValuePair(int id) {
        super(id);
    }

    public ASTMemberValuePair(JavaParser p, int id) {
        super(p, id);
    }


    
    public Object jjtAccept(JavaParserVisitor visitor, Object data) {
        return visitor.visit(this, data);
    }

    public void dump(String prefix) {
        System.out.println(toString(prefix) + ":" + super.getImage());
        dumpChildren(prefix);
    }

}
