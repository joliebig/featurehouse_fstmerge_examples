package net.sourceforge.pmd;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import net.sourceforge.pmd.ast.CompilationUnit;
import net.sourceforge.pmd.ast.SimpleNode;
import net.sourceforge.pmd.util.Benchmark;


public abstract class AbstractRuleChainVisitor implements RuleChainVisitor {
    
    protected List<Rule> rules = new ArrayList<Rule>();

    
    protected Map<String, List<SimpleNode>> nodeNameToNodes;

    
    public void add(Rule rule) {
        rules.add(rule);
    }

    
    public void visitAll(List<CompilationUnit> astCompilationUnits, RuleContext ctx) {
        initialize();
        clear();

        
        
        long start = System.nanoTime();
        indexNodes(astCompilationUnits, ctx);
        long end = System.nanoTime();
        Benchmark.mark(Benchmark.TYPE_RULE_CHAIN_VISIT, end - start, 1);

        
        int visits = 0;
        start = System.nanoTime();
        for (Rule rule: rules) {
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
        for (Iterator<Rule> i = rules.iterator(); i.hasNext();) {
            Rule rule = i.next();
            if (rule.usesRuleChain()) {
                visitedNodes.addAll(rule.getRuleChainVisits());
            }
            else {
                
                i.remove();
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
