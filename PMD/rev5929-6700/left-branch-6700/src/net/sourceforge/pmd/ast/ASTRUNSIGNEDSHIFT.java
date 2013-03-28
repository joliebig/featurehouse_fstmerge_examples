

package net.sourceforge.pmd.ast;

public class ASTRUNSIGNEDSHIFT extends SimpleJavaNode {
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
