

package net.sourceforge.pmd.ast;

public class ASTAdditiveExpression extends SimpleJavaTypeNode {
    public ASTAdditiveExpression(int id) {
        super(id);
    }

    public ASTAdditiveExpression(JavaParser p, int id) {
        super(p, id);
    }


    
    public Object jjtAccept(JavaParserVisitor visitor, Object data) {
        return visitor.visit(this, data);
    }

}
