
package net.sourceforge.pmd.lang.rule.xpath;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Stack;
import java.util.Map.Entry;
import java.util.logging.Level;
import java.util.logging.Logger;

import net.sourceforge.pmd.PropertyDescriptor;
import net.sourceforge.pmd.RuleContext;
import net.sourceforge.pmd.lang.ast.Node;

import org.jaxen.BaseXPath;
import org.jaxen.JaxenException;
import org.jaxen.Navigator;
import org.jaxen.SimpleVariableContext;
import org.jaxen.XPath;
import org.jaxen.expr.AllNodeStep;
import org.jaxen.expr.DefaultXPathFactory;
import org.jaxen.expr.Expr;
import org.jaxen.expr.LocationPath;
import org.jaxen.expr.NameStep;
import org.jaxen.expr.Predicate;
import org.jaxen.expr.Step;
import org.jaxen.expr.UnionExpr;
import org.jaxen.expr.XPathFactory;
import org.jaxen.saxpath.Axis;


public class JaxenXPathRuleQuery extends AbstractXPathRuleQuery {

    private static final Logger LOG = Logger.getLogger(JaxenXPathRuleQuery.class.getName());

    private static enum InitializationStatus {
	NONE, PARTIAL, FULL
    };

    
    private InitializationStatus initializationStatus = InitializationStatus.NONE;
    private Map<String, List<XPath>> nodeNameToXPaths;

    private static final String AST_ROOT = "_AST_ROOT_";

    
    @Override
    public boolean isSupportedVersion(String version) {
	return XPATH_1_0.equals(version);
    }

    
    @Override
    @SuppressWarnings("unchecked")
    public List<Node> evaluate(Node node, RuleContext data) {
	List<Node> results = new ArrayList<Node>();
	try {
	    initializeXPathExpression(data.getLanguageVersion().getLanguageVersionHandler().getXPathHandler()
		    .getNavigator());
	    List<XPath> xpaths = nodeNameToXPaths.get(node.toString());
	    if (xpaths == null) {
		xpaths = nodeNameToXPaths.get(AST_ROOT);
	    }
	    for (XPath xpath : xpaths) {
		List<Node> nodes = xpath.selectNodes(node);
		results.addAll(nodes);
	    }
	} catch (JaxenException ex) {
	    throw new RuntimeException(ex);
	}
	return results;
    }

    
    @Override
    public List<String> getRuleChainVisits() {
	try {
	    
	    initializeXPathExpression(null);
	    return super.getRuleChainVisits();
	} catch (JaxenException ex) {
	    throw new RuntimeException(ex);
	}
    }

    @SuppressWarnings("unchecked")
    private void initializeXPathExpression(Navigator navigator) throws JaxenException {
	if (initializationStatus == InitializationStatus.FULL
		|| (initializationStatus == InitializationStatus.PARTIAL && navigator == null)) {
	    return;
	}

	
	
	
	
	
	
	nodeNameToXPaths = new HashMap<String, List<XPath>>();

	BaseXPath originalXPath = createXPath(xpath, navigator);
	indexXPath(originalXPath, AST_ROOT);

	boolean useRuleChain = true;
	Stack<Expr> pending = new Stack<Expr>();
	pending.push(originalXPath.getRootExpr());
	while (!pending.isEmpty()) {
	    Expr node = pending.pop();

	    
	    boolean valid = false;

	    
	    if (node instanceof LocationPath) {
		LocationPath locationPath = (LocationPath) node;
		if (locationPath.isAbsolute()) {
		    
		    List<Step> steps = locationPath.getSteps();
		    if (steps.size() >= 2) {
			Step step1 = steps.get(0);
			Step step2 = steps.get(1);
			
			if (step1 instanceof AllNodeStep && ((AllNodeStep) step1).getAxis() == Axis.DESCENDANT_OR_SELF) {
			    
			    if (step2 instanceof NameStep && ((NameStep) step2).getAxis() == Axis.CHILD) {
				
				XPathFactory xpathFactory = new DefaultXPathFactory();

				
				LocationPath relativeLocationPath = xpathFactory.createRelativeLocationPath();
				
				Step allNodeStep = xpathFactory.createAllNodeStep(Axis.SELF);
				
				for (Iterator<Predicate> i = step2.getPredicates().iterator(); i.hasNext();) {
				    allNodeStep.addPredicate(i.next());
				}
				relativeLocationPath.addStep(allNodeStep);

				
				for (int i = 2; i < steps.size(); i++) {
				    relativeLocationPath.addStep(steps.get(i));
				}

				BaseXPath xpath = createXPath(relativeLocationPath.getText(), navigator);
				indexXPath(xpath, ((NameStep) step2).getLocalName());
				valid = true;
			    }
			}
		    }
		}
	    } else if (node instanceof UnionExpr) { 
		UnionExpr unionExpr = (UnionExpr) node;
		pending.push(unionExpr.getLHS());
		pending.push(unionExpr.getRHS());
		valid = true;
	    }
	    if (!valid) {
		useRuleChain = false;
		break;
	    }
	}

	if (useRuleChain) {
	    
	    super.ruleChainVisits.addAll(nodeNameToXPaths.keySet());
	} else {
	    
	    nodeNameToXPaths.clear();
	    indexXPath(originalXPath, AST_ROOT);
	    if (LOG.isLoggable(Level.FINE)) {
		LOG.log(Level.FINE, "Unable to use RuleChain for for XPath: " + xpath);
	    }
	}

	if (navigator == null) {
	    this.initializationStatus = InitializationStatus.PARTIAL;
	    
	    nodeNameToXPaths = null;
	} else {
	    this.initializationStatus = InitializationStatus.FULL;
	}

    }

    private void indexXPath(XPath xpath, String nodeName) {
	List<XPath> xpaths = nodeNameToXPaths.get(nodeName);
	if (xpaths == null) {
	    xpaths = new ArrayList<XPath>();
	    nodeNameToXPaths.put(nodeName, xpaths);
	}
	xpaths.add(xpath);
    }

    private BaseXPath createXPath(String xpathQueryString, Navigator navigator) throws JaxenException {
	BaseXPath xpath = new BaseXPath(xpathQueryString, navigator);
	if (properties.size() > 1) {
	    SimpleVariableContext vc = new SimpleVariableContext();
	    for (Entry<PropertyDescriptor<?>, Object> e : properties.entrySet()) {
		if (!"xpath".equals(e.getKey())) {
		    Object value = e.getValue();
		    vc.setVariableValue(e.getKey().name(), value != null ? value.toString() : null);
		}
	    }
	    xpath.setVariableContext(vc);
	}
	return xpath;
    }
}
