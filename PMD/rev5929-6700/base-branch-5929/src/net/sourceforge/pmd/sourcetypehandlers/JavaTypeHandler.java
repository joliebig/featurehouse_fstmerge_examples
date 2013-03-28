package net.sourceforge.pmd.sourcetypehandlers;

import net.sourceforge.pmd.ast.ASTCompilationUnit;
import net.sourceforge.pmd.dfa.DataFlowFacade;
import net.sourceforge.pmd.symboltable.SymbolFacade;
import net.sourceforge.pmd.typeresolution.TypeResolutionFacade;


public abstract class JavaTypeHandler implements SourceTypeHandler {

    public VisitorStarter getDataFlowFacade() {
        return new VisitorStarter() {
            public void start(Object rootNode) {
                new DataFlowFacade().initializeWith((ASTCompilationUnit) rootNode);
            }
        };
    }

    public VisitorStarter getSymbolFacade() {
        return new VisitorStarter() {
            public void start(Object rootNode) {
                new SymbolFacade().initializeWith((ASTCompilationUnit) rootNode);
            }
        };
    }
    
    public VisitorStarter getTypeResolutionFacade(final ClassLoader classLoader) {
        return new VisitorStarter() {
            public void start(Object rootNode) {
                new TypeResolutionFacade().initializeWith(classLoader, (ASTCompilationUnit) rootNode);
            }
        };
    }
    
    
}
