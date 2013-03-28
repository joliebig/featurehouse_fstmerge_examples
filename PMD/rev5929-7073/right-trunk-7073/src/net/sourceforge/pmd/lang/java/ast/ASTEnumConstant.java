

package net.sourceforge.pmd.lang.java.ast;

public class ASTEnumConstant extends AbstractJavaNode {
    public ASTEnumConstant(int id) {
        super(id);
    }

    public ASTEnumConstant(JavaParser p, int id) {
        super(p, id);
    }


    
    public Object jjtAccept(JavaParserVisitor visitor, Object data) {
        return visitor.visit(this, data);
    }
}
