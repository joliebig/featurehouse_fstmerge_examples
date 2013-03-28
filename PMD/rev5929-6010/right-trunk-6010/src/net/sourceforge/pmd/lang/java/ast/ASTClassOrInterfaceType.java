

package net.sourceforge.pmd.lang.java.ast;

public class ASTClassOrInterfaceType extends AbstractJavaTypeNode {
    public ASTClassOrInterfaceType(int id) {
        super(id);
    }

    public ASTClassOrInterfaceType(JavaParser p, int id) {
        super(p, id);
    }


    
    public Object jjtAccept(JavaParserVisitor visitor, Object data) {
        return visitor.visit(this, data);
    }
}
