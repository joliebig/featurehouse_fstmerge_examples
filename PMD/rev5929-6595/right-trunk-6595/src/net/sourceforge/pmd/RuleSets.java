package net.sourceforge.pmd;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import net.sourceforge.pmd.lang.Language;
import net.sourceforge.pmd.lang.ast.Node;


public class RuleSets {
    
    private Collection<RuleSet> ruleSets = new ArrayList<RuleSet>();

    
    private RuleChain ruleChain = new RuleChain();

    
    public RuleSets() {
    }

    
    public RuleSets(RuleSet ruleSet) {
	this();
	addRuleSet(ruleSet);
    }

    
    public void addRuleSet(RuleSet ruleSet) {
	ruleSets.add(ruleSet);
	ruleChain.add(ruleSet);
    }

    
    public RuleSet[] getAllRuleSets() {
	return ruleSets.toArray(new RuleSet[ruleSets.size()]);
    }

    public Iterator<RuleSet> getRuleSetsIterator() {
	return ruleSets.iterator();
    }

    
    public Set<Rule> getAllRules() {
	HashSet<Rule> result = new HashSet<Rule>();
	for (RuleSet r : ruleSets) {
	    result.addAll(r.getRules());
	}
	return result;
    }

    
    public boolean applies(File file) {
	for (RuleSet ruleSet : ruleSets) {
	    if (ruleSet.applies(file)) {
		return true;
	    }
	}
	return false;
    }

    
    public void start(RuleContext ctx) {
	for (RuleSet ruleSet : ruleSets) {
	    ruleSet.start(ctx);
	}
    }

    
    public void apply(List<Node> acuList, RuleContext ctx, Language language) {
	ruleChain.apply(acuList, ctx, language);
	for (RuleSet ruleSet : ruleSets) {
	    if (ruleSet.applies(ctx.getSourceCodeFile())) {
		ruleSet.apply(acuList, ctx);
	    }
	}
    }

    
    public void end(RuleContext ctx) {
	for (RuleSet ruleSet : ruleSets) {
	    ruleSet.end(ctx);
	}
    }

    
    public boolean usesDFA(Language language) {
	for (RuleSet ruleSet : ruleSets) {
	    if (ruleSet.usesDFA(language)) {
		return true;
	    }
	}
	return false;
    }

    
    public Rule getRuleByName(String ruleName) {
	Rule rule = null;
	for (Iterator<RuleSet> i = ruleSets.iterator(); i.hasNext() && rule == null;) {
	    RuleSet ruleSet = i.next();
	    rule = ruleSet.getRuleByName(ruleName);
	}
	return rule;
    }

    public boolean usesTypeResolution(Language language) {
	for (RuleSet ruleSet : ruleSets) {
	    if (ruleSet.usesTypeResolution(language)) {
		return true;
	    }
	}
	return false;
    }
}
