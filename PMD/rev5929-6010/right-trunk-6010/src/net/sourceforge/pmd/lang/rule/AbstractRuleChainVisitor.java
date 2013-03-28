package net.sourceforge.pmd.lang.rule;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import net.sourceforge.pmd.Rule;
import net.sourceforge.pmd.RuleContext;
import net.sourceforge.pmd.RuleReference;
import net.sourceforge.pmd.lang.ast.Node;
import net.sourceforge.pmd.util.Benchmark;


public abstract class AbstractRuleChainVisitor implements RuleChainVisitor {
    
    protected List<Rule> rules = new ArrayList<Rule>();

    
    protected Map<String, List<Node>> nodeNameToNodes;

    
    public void add(Rule rule) {
        rules.add(rule);
    }

    
    public void visitAll(List<Node> nodes, RuleContext ctx) {
        initialize();
        clear();

        
        
        long start = System.nanoTime();
        indexNodes(nodes, ctx);
        long end = System.nanoTime();
        Benchmark.mark(Benchmark.TYPE_RULE_CHAIN_VISIT, end - start, 1);

        
        int visits = 0;
        start = System.nanoTime();
        for (Rule rule: rules) {
            final List<String> nodeNames = rule.getRuleChainVisits();
            for (int j = 0; j < nodeNames.size(); j++) {
                List<Node> ns = nodeNameToNodes.get(nodeNames.get(j));
                for (Node node: ns) {
                    
                    while (rule instanceof RuleReference) {
                        rule = ((RuleReference)rule).getRule();
                    }
                    visit(rule, node, ctx);
                }
                visits += ns.size();
            }
            end = System.nanoTime();
            Benchmark.mark(Benchmark.TYPE_RULE_CHAIN_RULE, rule.getName(), end - start, visits);
            start = end;
        }
    }

    
    protected abstract void visit(Rule rule, Node node, RuleContext ctx);

    
    protected abstract void indexNodes(List<Node> nodes, RuleContext ctx);

    
    protected void indexNode(Node node) {
        List<Node> nodes = nodeNameToNodes.get(node.toString());
        if (nodes != null) {
            nodes.add(node);
        }
    }

    
    protected void initialize() {
        if (nodeNameToNodes != null) {
            return;
        }

        
        Set<String> visitedNodes = new HashSet<String>();
        for (Iterator<Rule> i = rules.iterator(); i.hasNext();) {
            Rule rule = i.next();
            if (rule.usesRuleChain()) {
                visitedNodes.addAll(rule.getRuleChainVisits());
            }
            else {
                
                i.remove();
            }
        }

        
        
        
        nodeNameToNodes = new HashMap<String, List<Node>>();
        for (String s: visitedNodes) {
            List<Node> nodes = new ArrayList<Node>(100);
            nodeNameToNodes.put(s, nodes);
        }
    }

    
    protected void clear() {
        for (List<Node> l: nodeNameToNodes.values()) {
            l.clear();
        }
    }
}
