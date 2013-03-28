

package net.sourceforge.pmd.lang.java.ast;

public class ASTClassOrInterfaceBody extends AbstractJavaNode {
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
