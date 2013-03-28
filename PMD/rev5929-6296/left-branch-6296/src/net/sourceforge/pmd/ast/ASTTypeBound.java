

package net.sourceforge.pmd.ast;

public class ASTTypeBound extends SimpleJavaNode {
    public ASTTypeBound(int id) {
        super(id);
    }

    public ASTTypeBound(JavaParser p, int id) {
        super(p, id);
    }


    
    public Object jjtAccept(JavaParserVisitor visitor, Object data) {
        return visitor.visit(this, data);
    }
}
