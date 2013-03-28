package net.sourceforge.pmd.lang.rule;

import net.sourceforge.pmd.Rule;
import net.sourceforge.pmd.RuleContext;
import net.sourceforge.pmd.lang.ast.Node;


public interface RuleViolationFactory {
    
    void addViolation(RuleContext ruleContext, Rule rule, Node node, String message, Object[] args);
}
