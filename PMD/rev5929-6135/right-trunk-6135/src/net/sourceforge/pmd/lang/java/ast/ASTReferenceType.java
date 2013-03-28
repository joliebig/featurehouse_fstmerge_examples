

package net.sourceforge.pmd.lang.java.ast;

public class ASTReferenceType extends AbstractJavaTypeNode implements Dimensionable {
    public ASTReferenceType(int id) {
        super(id);
    }

    public ASTReferenceType(JavaParser p, int id) {
        super(p, id);
    }


    
    public Object jjtAccept(JavaParserVisitor visitor, Object data) {
        return visitor.visit(this, data);
    }

    private int arrayDepth;

    public void bumpArrayDepth() {
        arrayDepth++;
    }

    public int getArrayDepth() {
        return arrayDepth;
    }

    public boolean isArray() {
        return arrayDepth > 0;
    }

}
