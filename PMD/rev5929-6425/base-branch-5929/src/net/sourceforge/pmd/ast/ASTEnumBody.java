

package net.sourceforge.pmd.ast;

public class ASTEnumBody extends SimpleJavaNode {
    public ASTEnumBody(int id) {
        super(id);
    }

    public ASTEnumBody(JavaParser p, int id) {
        super(p, id);
    }


    
    public Object jjtAccept(JavaParserVisitor visitor, Object data) {
        return visitor.visit(this, data);
    }
}
