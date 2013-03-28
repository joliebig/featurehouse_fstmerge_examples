
package net.sourceforge.pmd.lang.ast.xpath;

import net.sourceforge.pmd.lang.XPathHandler;

import org.jaxen.Navigator;

public abstract class AbstractASTXPathHandler implements XPathHandler {

    public Navigator getNavigator() {
	return new DocumentNavigator();
    }
}
