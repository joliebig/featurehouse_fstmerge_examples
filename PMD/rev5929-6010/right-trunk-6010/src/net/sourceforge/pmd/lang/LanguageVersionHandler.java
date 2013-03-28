
package net.sourceforge.pmd.lang;

import java.io.Writer;

import net.sourceforge.pmd.lang.rule.RuleViolationFactory;


public interface LanguageVersionHandler {
    
    
    XPathFunctionRegister getXPathFunctionRegister();

    
    RuleViolationFactory getRuleViolationFactory();

    
    Parser getParser();

    
    VisitorStarter getDataFlowFacade();

    
    VisitorStarter getSymbolFacade();

    
    VisitorStarter getTypeResolutionFacade(ClassLoader classLoader);

    
    VisitorStarter getDumpFacade(Writer writer, String prefix, boolean recurse);
}
