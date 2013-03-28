

package net.sourceforge.pmd.ast;

public class ASTForInit extends SimpleJavaNode {
    public ASTForInit(int id) {
        super(id);
    }

    public ASTForInit(JavaParser p, int id) {
        super(p, id);
    }


    
    public Object jjtAccept(JavaParserVisitor visitor, Object data) {
        return visitor.visit(this, data);
    }
}
