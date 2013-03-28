
package net.sourceforge.pmd.lang;

import java.io.Writer;


public abstract class AbstractLanguageVersionHandler implements LanguageVersionHandler {

    public DataFlowHandler getDataFlowHandler() {
	return DataFlowHandler.DUMMY;
    }

    public XPathHandler getXPathHandler() {
	return XPathHandler.DUMMY;
    }

    public ParserOptions getDefaultParserOptions() {
	return new ParserOptions();
    }

    public VisitorStarter getDataFlowFacade() {
	return VisitorStarter.DUMMY;
    }

    public VisitorStarter getSymbolFacade() {
	return VisitorStarter.DUMMY;
    }

    public VisitorStarter getTypeResolutionFacade(ClassLoader classLoader) {
	return VisitorStarter.DUMMY;
    }

    public VisitorStarter getDumpFacade(final Writer writer, final String prefix, final boolean recurse) {
	return VisitorStarter.DUMMY;
    }
}
