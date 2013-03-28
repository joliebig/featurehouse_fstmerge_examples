package net.sourceforge.pmd.lang.rule.xpath;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import net.sourceforge.pmd.PropertyDescriptor;
import net.sourceforge.pmd.RuleContext;
import net.sourceforge.pmd.lang.ast.Node;


public abstract class AbstractXPathRuleQuery implements XPathRuleQuery {

    
    protected String xpath;

    
    protected String version;

    
    protected Map<PropertyDescriptor<?>, Object> properties;

    
    protected final List<String> ruleChainVisits = new ArrayList<String>();

    
    public void setXPath(String xpath) {
	this.xpath = xpath;
    }

    
    public void setVersion(String version) throws UnsupportedOperationException {
	if (!isSupportedVersion(version)) {
	    throw new UnsupportedOperationException(this.getClass().getSimpleName()
		    + " does not support XPath version: " + version);
	}
	this.version = version;
    }

    
    protected abstract boolean isSupportedVersion(String version);

    
    public void setProperties(Map<PropertyDescriptor<?>, Object> properties) {
	this.properties = properties;
    }

    
    public List<String> getRuleChainVisits() {
	return ruleChainVisits;
    }

    
    public abstract List<Node> evaluate(Node node, RuleContext data);
}
