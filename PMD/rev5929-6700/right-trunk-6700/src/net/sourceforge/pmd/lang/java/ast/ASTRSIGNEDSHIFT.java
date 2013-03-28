

package net.sourceforge.pmd.lang.java.ast;

public class ASTRSIGNEDSHIFT extends AbstractJavaNode {
    public ASTRSIGNEDSHIFT(int id) {
        super(id);
    }

    public ASTRSIGNEDSHIFT(JavaParser p, int id) {
        super(p, id);
    }


    
    public Object jjtAccept(JavaParserVisitor visitor, Object data) {
        return visitor.visit(this, data);
    }
}
