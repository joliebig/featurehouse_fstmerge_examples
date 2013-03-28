

package net.sourceforge.pmd.ast;

public class ASTEnumConstant extends SimpleJavaNode {
    public ASTEnumConstant(int id) {
        super(id);
    }

    public ASTEnumConstant(JavaParser p, int id) {
        super(p, id);
    }


    
    public Object jjtAccept(JavaParserVisitor visitor, Object data) {
        return visitor.visit(this, data);
    }

    public void dump(String prefix) {
        System.out.println(toString(prefix) + getImage());
        dumpChildren(prefix);
    }
}
