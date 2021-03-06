package net.sourceforge.pmd.lang.rule;

import java.util.List;

import net.sourceforge.pmd.Rule;
import net.sourceforge.pmd.RuleContext;
import net.sourceforge.pmd.RuleSet;
import net.sourceforge.pmd.lang.ast.Node;


public interface RuleChainVisitor {
    
    void add(RuleSet ruleSet, Rule rule);

    
    void visitAll(List<Node> nodes, RuleContext ctx);
}
