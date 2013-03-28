package net.sourceforge.pmd.lang.java.xpath;

import net.sf.saxon.expr.XPathContext;
import net.sourceforge.pmd.lang.ast.Node;
import net.sourceforge.pmd.lang.ast.xpath.saxon.ElementNode;


public class JavaFunctions {
    public static boolean typeof(XPathContext context, String nodeTypeName, String fullTypeName) {
	return typeof(context, nodeTypeName, fullTypeName, null);
    }

    public static boolean typeof(XPathContext context, String nodeTypeName, String fullTypeName, String shortTypeName) {
	return TypeOfFunction.typeof((Node) ((ElementNode) context.getContextItem()).getUnderlyingNode(), nodeTypeName,
		fullTypeName, shortTypeName);
    }
}
