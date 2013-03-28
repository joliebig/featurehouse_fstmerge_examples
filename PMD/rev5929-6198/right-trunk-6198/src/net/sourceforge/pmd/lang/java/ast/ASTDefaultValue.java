

package net.sourceforge.pmd.lang.java.ast;

public class ASTDefaultValue extends AbstractJavaNode {
    public ASTDefaultValue(int id) {
        super(id);
    }

    public ASTDefaultValue(JavaParser p, int id) {
        super(p, id);
    }


    
    public Object jjtAccept(JavaParserVisitor visitor, Object data) {
        return visitor.visit(this, data);
    }
}
