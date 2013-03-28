

package net.sourceforge.pmd.lang.java.ast;

public class ASTNameList extends AbstractJavaNode {
    public ASTNameList(int id) {
        super(id);
    }

    public ASTNameList(JavaParser p, int id) {
        super(p, id);
    }


    
    public Object jjtAccept(JavaParserVisitor visitor, Object data) {
        return visitor.visit(this, data);
    }
}
