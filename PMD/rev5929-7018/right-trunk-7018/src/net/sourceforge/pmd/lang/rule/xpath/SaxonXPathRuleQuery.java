package net.sourceforge.pmd.lang.rule.xpath;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.sf.saxon.om.ValueRepresentation;
import net.sf.saxon.sxpath.AbstractStaticContext;
import net.sf.saxon.sxpath.IndependentContext;
import net.sf.saxon.sxpath.XPathDynamicContext;
import net.sf.saxon.sxpath.XPathEvaluator;
import net.sf.saxon.sxpath.XPathExpression;
import net.sf.saxon.sxpath.XPathStaticContext;
import net.sf.saxon.sxpath.XPathVariable;
import net.sf.saxon.trans.XPathException;
import net.sf.saxon.value.BooleanValue;
import net.sf.saxon.value.Int64Value;
import net.sf.saxon.value.StringValue;
import net.sourceforge.pmd.PropertyDescriptor;
import net.sourceforge.pmd.RuleContext;
import net.sourceforge.pmd.lang.ast.Node;
import net.sourceforge.pmd.lang.ast.xpath.saxon.DocumentNode;
import net.sourceforge.pmd.lang.ast.xpath.saxon.ElementNode;
import net.sourceforge.pmd.lang.rule.properties.BooleanProperty;
import net.sourceforge.pmd.lang.rule.properties.EnumeratedProperty;
import net.sourceforge.pmd.lang.rule.properties.IntegerProperty;
import net.sourceforge.pmd.lang.rule.properties.PropertyDescriptorWrapper;
import net.sourceforge.pmd.lang.rule.properties.StringProperty;
import net.sourceforge.pmd.lang.xpath.Initializer;


public class SaxonXPathRuleQuery extends AbstractXPathRuleQuery {

    
    private XPathExpression xpathExpression;
    private List<XPathVariable> xpathVariables;

    
    @Override
    public boolean isSupportedVersion(String version) {
	return XPATH_1_0_COMPATIBILITY.equals(version) || XPATH_2_0.equals(version);
    }

    
    @Override
    @SuppressWarnings("unchecked")
    public List<Node> evaluate(Node node, RuleContext data) {
	initializeXPathExpression();

	List<Node> results = new ArrayList<Node>();
	try {
	    
	    DocumentNode documentNode = getDocumentNode(node);

	    
	    ElementNode rootElementNode = documentNode.nodeToElementNode.get(node);

	    
	    XPathDynamicContext xpathDynamicContext = xpathExpression.createDynamicContext(rootElementNode);

	    
	    for (XPathVariable xpathVariable : xpathVariables) {
		String name = xpathVariable.getVariableQName().getLocalName();
		for (Map.Entry<PropertyDescriptor<?>, Object> entry : super.properties.entrySet()) {
		    if (name.equals(entry.getKey().name())) {
			PropertyDescriptor<?> propertyDescriptor = entry.getKey();
			if (propertyDescriptor instanceof PropertyDescriptorWrapper) {
			    propertyDescriptor = ((PropertyDescriptorWrapper) propertyDescriptor)
				    .getPropertyDescriptor();
			}
			Object value = entry.getValue();
			ValueRepresentation valueRepresentation;

			
			
			if (propertyDescriptor instanceof StringProperty) {
			    valueRepresentation = new StringValue((String) value);
			} else if (propertyDescriptor instanceof BooleanProperty) {
			    valueRepresentation = BooleanValue.get(((Boolean) value).booleanValue());
			} else if (propertyDescriptor instanceof IntegerProperty) {
			    valueRepresentation = Int64Value.makeIntegerValue((Integer) value);
			} else if (propertyDescriptor instanceof EnumeratedProperty) {
			    if (value instanceof String) {
				valueRepresentation = new StringValue((String) value);
			    } else {
				throw new RuntimeException(
					"Unable to create ValueRepresentaton for non-String EnumeratedProperty value: "
						+ value);
			    }
			} else {
			    throw new RuntimeException("Unable to create ValueRepresentaton for PropertyDescriptor: "
				    + propertyDescriptor);
			}
			xpathDynamicContext.setVariable(xpathVariable, valueRepresentation);
		    }
		}
	    }

	    List<ElementNode> nodes = xpathExpression.evaluate(xpathDynamicContext);
	    for (ElementNode elementNode : nodes) {
		results.add((Node) elementNode.getUnderlyingNode());
	    }
	} catch (XPathException e) {
	    throw new RuntimeException(super.xpath + " had problem: " + e.getMessage(), e);
	}
	return results;
    }

    private static final Map<Node, DocumentNode> CACHE = new HashMap<Node, DocumentNode>();

    private DocumentNode getDocumentNode(Node node) {
	
	Node root = node;
	while (root.jjtGetParent() != null) {
	    root = root.jjtGetParent();
	}

	
	
	DocumentNode documentNode;
	synchronized (CACHE) {
	    documentNode = CACHE.get(root);
	    if (documentNode == null) {
		documentNode = new DocumentNode(root);
		if (CACHE.size() > 20) {
		    CACHE.clear();
		}
		CACHE.put(root, documentNode);
	    }
	}
	return documentNode;
    }

    private void initializeXPathExpression() {
	if (xpathExpression != null) {
	    return;
	}
	try {
	    XPathEvaluator xpathEvaluator = new XPathEvaluator();
	    XPathStaticContext xpathStaticContext = xpathEvaluator.getStaticContext();

	    
	    if (XPATH_1_0_COMPATIBILITY.equals(version)) {
		((AbstractStaticContext) xpathStaticContext).setBackwardsCompatibilityMode(true);
	    }

	    
	    Initializer.initialize((IndependentContext) xpathStaticContext);

	    
	    
	    
	    xpathVariables = new ArrayList<XPathVariable>();
	    for (PropertyDescriptor<?> propertyDescriptor : super.properties.keySet()) {
		String name = propertyDescriptor.name();
		if (!"xpath".equals(name)) {
		    XPathVariable xpathVariable = xpathStaticContext.declareVariable(null, name);
		    xpathVariables.add(xpathVariable);
		}
	    }

	    
	    
	    
	    xpathExpression = xpathEvaluator.createExpression(super.xpath);
	} catch (XPathException e) {
	    throw new RuntimeException(e);
	}
    }
}
