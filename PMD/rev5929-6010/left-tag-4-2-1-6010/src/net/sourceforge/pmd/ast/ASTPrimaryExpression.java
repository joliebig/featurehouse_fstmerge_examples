

package net.sourceforge.pmd.ast;

public class ASTPrimaryExpression extends SimpleJavaTypeNode {
    public ASTPrimaryExpression(int id) {
        super(id);
    }

    public ASTPrimaryExpression(JavaParser p, int id) {
        super(p, id);
    }


    
    public Object jjtAccept(JavaParserVisitor visitor, Object data) {
        return visitor.visit(this, data);
    }

}
