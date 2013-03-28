package net.sourceforge.pmd.symboltable;

import net.sourceforge.pmd.lang.VisitorStarter;
import net.sourceforge.pmd.lang.ast.Node;
import net.sourceforge.pmd.lang.jsp.ast.ASTCompilationUnit;


public class JspSymbolFacade implements VisitorStarter {

    
    public void start(Node rootNode) {
        ASTCompilationUnit compilationUnit = (ASTCompilationUnit) rootNode;
        new JspScopeAndDeclarationFinder().setJspScope(compilationUnit);
    }

}
