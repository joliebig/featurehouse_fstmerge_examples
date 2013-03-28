

package net.sourceforge.pmd.lang.java.ast;

public class ASTRUNSIGNEDSHIFT extends AbstractJavaNode {
    public ASTRUNSIGNEDSHIFT(int id) {
        super(id);
    }

    public ASTRUNSIGNEDSHIFT(JavaParser p, int id) {
        super(p, id);
    }


    
    public Object jjtAccept(JavaParserVisitor visitor, Object data) {
        return visitor.visit(this, data);
    }
}
