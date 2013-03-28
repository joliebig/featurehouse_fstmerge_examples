package net.sourceforge.pmd.symboltable;

import net.sourceforge.pmd.jsp.ast.ASTCompilationUnit;
import net.sourceforge.pmd.sourcetypehandlers.VisitorStarter;


public class JspSymbolFacade implements VisitorStarter {

    
    public void start(Object rootNode) {
        ASTCompilationUnit compilationUnit = (ASTCompilationUnit) rootNode;
        new JspScopeAndDeclarationFinder().setJspScope(compilationUnit);
    }

}
