

package net.sourceforge.pmd.lang.jsp.ast;

import net.sourceforge.pmd.lang.ast.RootNode;

public class ASTCompilationUnit extends AbstractJspNode implements RootNode {
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
