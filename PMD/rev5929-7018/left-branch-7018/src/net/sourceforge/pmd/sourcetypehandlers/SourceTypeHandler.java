package net.sourceforge.pmd.sourcetypehandlers;

import net.sourceforge.pmd.parsers.Parser;


public interface SourceTypeHandler {

    
    Parser getParser();

    
    VisitorStarter getDataFlowFacade();

    
    VisitorStarter getSymbolFacade();
    
    
    VisitorStarter getTypeResolutionFacade(ClassLoader classLoader);
    
        
}
