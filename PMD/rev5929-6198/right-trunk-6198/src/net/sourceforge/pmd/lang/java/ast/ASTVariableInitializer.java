

package net.sourceforge.pmd.lang.java.ast;

public class ASTVariableInitializer extends AbstractJavaNode {
    public ASTVariableInitializer(int id) {
        super(id);
    }

    public ASTVariableInitializer(JavaParser p, int id) {
        super(p, id);
    }


    
    public Object jjtAccept(JavaParserVisitor visitor, Object data) {
        return visitor.visit(this, data);
    }
}
