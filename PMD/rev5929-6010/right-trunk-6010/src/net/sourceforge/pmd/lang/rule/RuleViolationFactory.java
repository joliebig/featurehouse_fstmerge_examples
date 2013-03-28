package net.sourceforge.pmd.lang.rule;

import net.sourceforge.pmd.Rule;
import net.sourceforge.pmd.RuleContext;
import net.sourceforge.pmd.lang.ast.Node;


public interface RuleViolationFactory {
    
    void addViolation(RuleContext ruleContext, Rule rule, Node node);

    
    void addViolationWithMessage(RuleContext ruleContext, Rule rule, Node node, String message);

    
    void addViolation(RuleContext ruleContext, Rule rule, Node node, String arg);

    
    void addViolation(RuleContext ruleContext, Rule rule, Node node, Object[] args);
}
