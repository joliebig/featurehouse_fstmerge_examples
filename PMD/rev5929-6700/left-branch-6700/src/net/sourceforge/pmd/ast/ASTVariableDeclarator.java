

package net.sourceforge.pmd.ast;

public class ASTVariableDeclarator extends SimpleJavaTypeNode {
    public ASTVariableDeclarator(int id) {
        super(id);
    }

    public ASTVariableDeclarator(JavaParser p, int id) {
        super(p, id);
    }


    
    public Object jjtAccept(JavaParserVisitor visitor, Object data) {
        return visitor.visit(this, data);
    }
}
