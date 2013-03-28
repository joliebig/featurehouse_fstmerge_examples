

package net.sourceforge.pmd.ast;

public class ASTClassOrInterfaceType extends SimpleJavaTypeNode {
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
