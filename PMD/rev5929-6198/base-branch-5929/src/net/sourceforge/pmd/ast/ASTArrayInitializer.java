

package net.sourceforge.pmd.ast;

public class ASTArrayInitializer extends SimpleJavaNode {
    public ASTArrayInitializer(int id) {
        super(id);
    }

    public ASTArrayInitializer(JavaParser p, int id) {
        super(p, id);
    }


    
    public Object jjtAccept(JavaParserVisitor visitor, Object data) {
        return visitor.visit(this, data);
    }
}
