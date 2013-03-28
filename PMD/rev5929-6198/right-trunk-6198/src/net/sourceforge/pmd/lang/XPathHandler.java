package net.sourceforge.pmd.lang;

import net.sourceforge.pmd.lang.xpath.Initializer;

import org.jaxen.Navigator;


public interface XPathHandler {

    XPathHandler DUMMY = new XPathHandler() {
	public void initialize() {
	}

	public Navigator getNavigator() {
	    return null;
	}
    };

    
    void initialize();

    
    Navigator getNavigator();
}
