

package net.sourceforge.pmd.jsp.ast;

import net.sourceforge.pmd.ast.CompilationUnit;

public class ASTCompilationUnit extends SimpleNode implements CompilationUnit {
    public ASTCompilationUnit(int id) {
        super(id);
    }

    public ASTCompilationUnit(JspParser p, int id) {
        super(p, id);
    }


    
    public Object jjtAccept(JspParserVisitor visitor, Object data) {
        return visitor.visit(this, data);
    }
}
