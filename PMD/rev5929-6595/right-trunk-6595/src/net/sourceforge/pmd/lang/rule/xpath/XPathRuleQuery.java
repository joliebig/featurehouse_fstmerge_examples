
package net.sourceforge.pmd.lang.rule.xpath;

import java.util.List;
import java.util.Map;

import net.sourceforge.pmd.PropertyDescriptor;
import net.sourceforge.pmd.RuleContext;
import net.sourceforge.pmd.lang.ast.Node;


public interface XPathRuleQuery {

    
    String XPATH_1_0 = "1.0";

    
    String XPATH_1_0_COMPATIBILITY = "1.0 compatibility";

    
    String XPATH_2_0 = "2.0";

    
    void setXPath(String xpath);

    
    void setVersion(String version) throws UnsupportedOperationException;

    
    void setProperties(Map<PropertyDescriptor<?>, Object> properties);

    
    List<String> getRuleChainVisits();

    
    List<Node> evaluate(Node node, RuleContext data);
}
