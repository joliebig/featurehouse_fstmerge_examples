package net.sourceforge.pmd;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import net.sourceforge.pmd.ast.CompilationUnit;
import net.sourceforge.pmd.ast.SimpleNode;
import net.sourceforge.pmd.util.Benchmark;


public abstract class AbstractRuleChainVisitor implements RuleChainVisitor {
    
    protected Map<RuleSet, List<Rule>> ruleSetRules = new LinkedHashMap<RuleSet, List<Rule>>();

    
    protected Map<String, List<SimpleNode>> nodeNameToNodes;

    
    public void add(RuleSet ruleSet, Rule rule) {
	if (!ruleSetRules.containsKey(ruleSet)) {
	    ruleSetRules.put(ruleSet, new ArrayList<Rule>());
	}
	ruleSetRules.get(ruleSet).add(rule);
    }

    
    public void visitAll(List<CompilationUnit> astCompilationUnits, RuleContext ctx) {
        initialize();
        clear();

        
        
        long start = System.nanoTime();
        indexNodes(astCompilationUnits, ctx);
        long end = System.nanoTime();
        Benchmark.mark(Benchmark.TYPE_RULE_CHAIN_VISIT, end - start, 1);

        
        for (RuleSet ruleSet : ruleSetRules.keySet()) {
            if (!ruleSet.applies(ctx.getSourceCodeFile())) {
        	continue;
            }
            
            start = System.nanoTime();
            for (Rule rule: ruleSetRules.get(ruleSet)) {
                int visits = 0;
                final List<String> nodeNames = rule.getRuleChainVisits();
                for (int j = 0; j < nodeNames.size(); j++) {
                    List<SimpleNode> nodes = nodeNameToNodes.get(nodeNames.get(j));
                    for (SimpleNode node: nodes) {
                        
                        while (rule instanceof RuleReference) {
                            rule = ((RuleReference)rule).getRule();
                        }
                        visit(rule, node, ctx);
                    }
                    visits += nodes.size();
                }
                end = System.nanoTime();
                Benchmark.mark(Benchmark.TYPE_RULE_CHAIN_RULE, rule.getName(), end - start, visits);
                start = end;
            }
        }
    }

    
    protected abstract void visit(Rule rule, SimpleNode node, RuleContext ctx);

    
    protected abstract void indexNodes(List<CompilationUnit> astCompilationUnits, RuleContext ctx);

    
    protected void indexNode(SimpleNode node) {
        List<SimpleNode> nodes = nodeNameToNodes.get(node.toString());
        if (nodes != null) {
            nodes.add(node);
        }
    }

    
    protected void initialize() {
        if (nodeNameToNodes != null) {
            return;
        }

        
        Set<String> visitedNodes = new HashSet<String>();
        for (Iterator<Map.Entry<RuleSet, List<Rule>>> entryIterator = ruleSetRules.entrySet().iterator(); entryIterator.hasNext();) {
            Map.Entry<RuleSet, List<Rule>> entry = entryIterator.next();
            for (Iterator<Rule> ruleIterator = entry.getValue().iterator(); ruleIterator.hasNext();) {
                Rule rule = ruleIterator.next();
                if (rule.usesRuleChain()) {
                    visitedNodes.addAll(rule.getRuleChainVisits());
                }
                else {
                    
                    ruleIterator.remove();
                }
            }
            
            if (entry.getValue().isEmpty()) {
        	entryIterator.remove();
            }
        }

        
        
        
        nodeNameToNodes = new HashMap<String, List<SimpleNode>>();
        for (String s: visitedNodes) {
            List<SimpleNode> nodes = new ArrayList<SimpleNode>(100);
            nodeNameToNodes.put(s, nodes);
        }
    }

    
    protected void clear() {
        for (List<SimpleNode> l: nodeNameToNodes.values()) {
            l.clear();
        }
    }
}
