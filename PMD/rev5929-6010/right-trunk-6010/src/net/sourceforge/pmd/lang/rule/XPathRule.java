
package net.sourceforge.pmd.lang.rule;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Stack;
import java.util.Map.Entry;

import net.sourceforge.pmd.RuleContext;
import net.sourceforge.pmd.jaxen.DocumentNavigator;
import net.sourceforge.pmd.jaxen.Functions;
import net.sourceforge.pmd.lang.ast.Node;

import org.jaxen.BaseXPath;
import org.jaxen.JaxenException;
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


public class XPathRule extends AbstractRule {

    
    private Map<String, List<XPath>> nodeNameToXPaths;
    private boolean functionsRegistered;

    private static final String AST_ROOT = "_AST_ROOT_";

    
    public void evaluate(Node compilationUnit, RuleContext data) {
        try {
            initializeXPathExpression();
            List<XPath> xpaths = nodeNameToXPaths.get(compilationUnit.toString());
            if (xpaths == null) {
                xpaths = nodeNameToXPaths.get(AST_ROOT);
            }
            for (XPath xpath: xpaths) {
                List results = xpath.selectNodes(compilationUnit);
                for (Iterator j = results.iterator(); j.hasNext();) {
                    Node n = (Node) j.next();
                    addViolation(data, n, n.getImage());
                }
            }
        } catch (JaxenException ex) {
            throw new RuntimeException(ex);
        }
    }

    public List<String> getRuleChainVisits() {
        try {
            initializeXPathExpression();
            return super.getRuleChainVisits();
        } catch (JaxenException ex) {
            throw new RuntimeException(ex);
        }
    }

    private void initializeXPathExpression() throws JaxenException {
        if (nodeNameToXPaths != null) {
            return;
        }

        if (!functionsRegistered) {
            Functions.registerAll();
            functionsRegistered = true;
        }

        
        
        
        
        
        
        nodeNameToXPaths = new HashMap<String, List<XPath>>();

        BaseXPath originalXPath = createXPath(getStringProperty("xpath"));
        indexXPath(originalXPath, AST_ROOT);

        boolean useRuleChain = true;
        Stack<Expr> pending = new Stack<Expr>();
        pending.push(originalXPath.getRootExpr());
        while (!pending.isEmpty()) {
            Expr node = pending.pop();

            
            boolean valid = false;

            
            if (node instanceof LocationPath) {
                LocationPath locationPath = (LocationPath)node;
                if (locationPath.isAbsolute()) {
                    
                    List steps = locationPath.getSteps();
                    if (steps.size() >= 2) {
                        Step step1 = (Step)steps.get(0);
                        Step step2 = (Step)steps.get(1);
                        
                        if (step1 instanceof AllNodeStep && ((AllNodeStep)step1).getAxis() == Axis.DESCENDANT_OR_SELF) {
                            
                            if (step2 instanceof NameStep && ((NameStep)step2).getAxis() == Axis.CHILD) {
                                
                                XPathFactory xpathFactory = new DefaultXPathFactory();

                                
                                LocationPath relativeLocationPath = xpathFactory.createRelativeLocationPath();
                                
                                Step allNodeStep = xpathFactory.createAllNodeStep(Axis.SELF);
                                
                                for (Iterator i = step2.getPredicates().iterator(); i.hasNext();) {
                                    allNodeStep.addPredicate((Predicate)i.next());
                                }
                                relativeLocationPath.addStep(allNodeStep);

                                
                                for (int i = 2; i < steps.size(); i++) {
                                    relativeLocationPath.addStep((Step)steps.get(i));
                                }

                                BaseXPath xpath = createXPath(relativeLocationPath.getText());
                                indexXPath(xpath, ((NameStep)step2).getLocalName());
                                valid = true;
                            }
                        }
                    }
                }
            } else if (node instanceof UnionExpr) { 
                UnionExpr unionExpr = (UnionExpr)node;
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
            
            for (String s: nodeNameToXPaths.keySet()) {
                addRuleChainVisit(s);
            }
        } else { 
            nodeNameToXPaths.clear();
            indexXPath(originalXPath, AST_ROOT);
            
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

    private BaseXPath createXPath(String xpathQueryString) throws JaxenException {
        
        
        
        
        
        
        xpathQueryString = xpathQueryString.replaceAll("\"\"\"", "'\"'");

        BaseXPath xpath = new BaseXPath(xpathQueryString, new DocumentNavigator());
        if (getProperties().size() > 1) {
            SimpleVariableContext vc = new SimpleVariableContext();
            for (Entry e: getProperties().entrySet()) {
                if (!"xpath".equals(e.getKey())) {
                    vc.setVariableValue((String) e.getKey(), e.getValue());
                }
            }
            xpath.setVariableContext(vc);
        }
        return xpath;
    }

    
    public void apply(List<Node> nodes, RuleContext ctx) {
	for (Node node : nodes) {
            evaluate(node, ctx);
        }
    }
}
