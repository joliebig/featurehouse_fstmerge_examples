package net.sourceforge.pmd.symboltable;

import net.sourceforge.pmd.lang.jsp.ast.ASTCompilationUnit;



public class JspScopeAndDeclarationFinder {

    
    public void setJspScope(ASTCompilationUnit compilationUnit) {
        compilationUnit.setScope(new DummyScope());
    }
}
