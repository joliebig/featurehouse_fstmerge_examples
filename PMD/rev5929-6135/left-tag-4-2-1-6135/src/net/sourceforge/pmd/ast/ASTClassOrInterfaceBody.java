

package net.sourceforge.pmd.ast;

public class ASTClassOrInterfaceBody extends SimpleJavaNode {
    public ASTClassOrInterfaceBody(int id) {
        super(id);
    }

    public ASTClassOrInterfaceBody(JavaParser p, int id) {
        super(p, id);
    }


    
    public Object jjtAccept(JavaParserVisitor visitor, Object data) {
        return visitor.visit(this, data);
    }
}
