package net.sourceforge.pmd.lang;

import net.sf.saxon.sxpath.IndependentContext;
import net.sourceforge.pmd.lang.xpath.Initializer;

import org.jaxen.Navigator;


public interface XPathHandler {

    XPathHandler DUMMY = new XPathHandler() {
	public void initialize() {
	}

	public void initialize(IndependentContext context) {
	}

	public Navigator getNavigator() {
	    return null;
	}
    };

    
    void initialize();

    
    void initialize(IndependentContext context);

    
    Navigator getNavigator();
}
