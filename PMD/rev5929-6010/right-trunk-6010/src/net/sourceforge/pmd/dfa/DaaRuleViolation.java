
package net.sourceforge.pmd.dfa;

import net.sourceforge.pmd.Rule;
import net.sourceforge.pmd.RuleContext;
import net.sourceforge.pmd.lang.ast.Node;
import net.sourceforge.pmd.lang.java.ast.JavaNode;
import net.sourceforge.pmd.lang.java.rule.JavaRuleViolation;


public class DaaRuleViolation extends JavaRuleViolation {
    private String variableName;
    private int beginLine;
    private int endLine;
    private String type;
    
    public DaaRuleViolation(Rule rule, RuleContext ctx, Node node, String type, String msg, String var, int beginLine, int endLine) {
        super(rule, ctx, (JavaNode)node, msg);
        this.variableName = var;
        this.beginLine = beginLine;
        this.endLine = endLine;
        this.type = type;
    }
	
    public String getVariableName() {
        return variableName;
    }
	
    public int getBeginLine() {
        return beginLine;
    }
	
    public int getEndLine() {
        return endLine;
    }
    
    public String getType() {
        return type;
    }
}
