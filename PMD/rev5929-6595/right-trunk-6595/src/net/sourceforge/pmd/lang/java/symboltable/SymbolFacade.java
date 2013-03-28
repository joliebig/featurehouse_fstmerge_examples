
package net.sourceforge.pmd.lang.java.symboltable;

import net.sourceforge.pmd.lang.java.ast.ASTCompilationUnit;

public class SymbolFacade {
    public void initializeWith(ASTCompilationUnit node) {
        ScopeAndDeclarationFinder sc = new ScopeAndDeclarationFinder();
        node.jjtAccept(sc, null);
        OccurrenceFinder of = new OccurrenceFinder();
        node.jjtAccept(of, null);
    }
}
