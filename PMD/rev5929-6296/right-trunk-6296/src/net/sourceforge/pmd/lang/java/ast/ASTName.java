

package net.sourceforge.pmd.lang.java.ast;

import net.sourceforge.pmd.lang.java.symboltable.NameDeclaration;

public class ASTName extends AbstractJavaTypeNode {
    public ASTName(int id) {
        super(id);
    }

    public ASTName(JavaParser p, int id) {
        super(p, id);
    }

    private NameDeclaration nd;

    public void setNameDeclaration(NameDeclaration nd) {
        this.nd = nd;
    }

    public NameDeclaration getNameDeclaration() {
        return this.nd;
    }


    
    public Object jjtAccept(JavaParserVisitor visitor, Object data) {
        return visitor.visit(this, data);
    }
}
