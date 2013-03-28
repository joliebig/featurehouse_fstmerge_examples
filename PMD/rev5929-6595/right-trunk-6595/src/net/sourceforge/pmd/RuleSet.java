
package net.sourceforge.pmd;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import net.sourceforge.pmd.lang.Language;
import net.sourceforge.pmd.lang.LanguageVersion;
import net.sourceforge.pmd.lang.ast.Node;
import net.sourceforge.pmd.lang.rule.RuleReference;
import net.sourceforge.pmd.util.Benchmark;
import net.sourceforge.pmd.util.filter.Filter;
import net.sourceforge.pmd.util.filter.Filters;



public class RuleSet {

    private List<Rule> rules = new ArrayList<Rule>();
    private String fileName;
    private String name = "";
    private String description = "";
    private List<String> excludePatterns = new ArrayList<String>(0);
    private List<String> includePatterns = new ArrayList<String>(0);
    private Filter<File> filter;

    
    public int size() {
	return rules.size();
    }

    
    public void addRule(Rule rule) {
	if (rule == null) {
	    throw new RuntimeException("Null Rule reference added to a RuleSet; that's a bug somewhere in PMD");
	}
	rules.add(rule);
    }

    
    public void addRuleByReference(String ruleSetFileName, Rule rule) {
	if (ruleSetFileName == null) {
	    throw new RuntimeException("Adding a rule by reference is not allowed with a null rule set file name.");
	}
	if (rule == null) {
	    throw new RuntimeException("Null Rule reference added to a RuleSet; that's a bug somewhere in PMD");
	}
	if (!(rule instanceof RuleReference)) {
	    RuleSetReference ruleSetReference = new RuleSetReference();
	    ruleSetReference.setRuleSetFileName(ruleSetFileName);
	    RuleReference ruleReference = new RuleReference();
	    ruleReference.setRule(rule);
	    ruleReference.setRuleSetReference(ruleSetReference);
	    rule = ruleReference;
	}
	rules.add(rule);
    }

    
    public Collection<Rule> getRules() {
	return rules;
    }

    
    public boolean usesDFA(Language language) {
	for (Rule r : rules) {
	    if (r.getLanguage().equals(language)) {
		if (r.usesDFA()) {
		    return true;
		}
	    }
	}
	return false;
    }

    
    public Rule getRuleByName(String ruleName) {
	Rule rule = null;
	for (Iterator<Rule> i = rules.iterator(); i.hasNext() && rule == null;) {
	    Rule r = i.next();
	    if (r.getName().equals(ruleName)) {
		rule = r;
	    }
	}
	return rule;
    }

    
    public void addRuleSet(RuleSet ruleSet) {
	rules.addAll(rules.size(), ruleSet.getRules());
    }

    
    public void addRuleSetByReference(RuleSet ruleSet, boolean allRules) {
	if (ruleSet.getFileName() == null) {
	    throw new RuntimeException("Adding a rule by reference is not allowed with a null rule set file name.");
	}
	RuleSetReference ruleSetReference = new RuleSetReference();
	ruleSetReference.setRuleSetFileName(ruleSet.getFileName());
	ruleSetReference.setAllRules(allRules);
	for (Rule rule : ruleSet.getRules()) {
	    RuleReference ruleReference = new RuleReference();
	    ruleReference.setRule(rule);
	    ruleReference.setRuleSetReference(ruleSetReference);
	    rules.add(ruleReference);
	}
    }

    
    public boolean applies(File file) {
	
	if (filter == null) {
	    Filter<String> regexFilter = Filters.buildRegexFilterIncludeOverExclude(includePatterns, excludePatterns);
	    filter = Filters.toNormalizedFileFilter(regexFilter);
	}

	return file != null ? filter.filter(file) : true;
    }

    public void start(RuleContext ctx) {
	for (Rule rule : rules) {
	    rule.start(ctx);
	}
    }

    public void apply(List<? extends Node> acuList, RuleContext ctx) {
	long start = System.nanoTime();
	for (Rule rule : rules) {
	    if (!rule.usesRuleChain() && applies(rule, ctx.getLanguageVersion())) {
		rule.apply(acuList, ctx);
		long end = System.nanoTime();
		Benchmark.mark(Benchmark.TYPE_RULE, rule.getName(), end - start, 1);
		start = end;
	    }
	}
    }

    
    public static boolean applies(Rule rule, LanguageVersion languageVersion) {
	final LanguageVersion min = rule.getMinimumLanguageVersion();
	final LanguageVersion max = rule.getMinimumLanguageVersion();
	return rule.getLanguage().equals(languageVersion.getLanguage())
		&& (min == null || min.compareTo(languageVersion) <= 0)
		&& (max == null || max.compareTo(languageVersion) >= 0);
    }

    public void end(RuleContext ctx) {
	for (Rule rule : rules) {
	    rule.end(ctx);
	}
    }

    
    @Override
    public boolean equals(Object o) {
	if (!(o instanceof RuleSet)) {
	    return false; 
	}

	if (this == o) {
	    return true; 
	}

	RuleSet ruleSet = (RuleSet) o;
	return this.getName().equals(ruleSet.getName()) && this.getRules().equals(ruleSet.getRules());
    }

    
    @Override
    public int hashCode() {
	return this.getName().hashCode() + 13 * this.getRules().hashCode();
    }

    public String getFileName() {
	return fileName;
    }

    public void setFileName(String fileName) {
	this.fileName = fileName;
    }

    public String getName() {
	return name;
    }

    public void setName(String name) {
	this.name = name;
    }

    public String getDescription() {
	return description;
    }

    public void setDescription(String description) {
	this.description = description;
    }

    public List<String> getExcludePatterns() {
	return this.excludePatterns;
    }

    public void addExcludePattern(String excludePattern) {
	this.excludePatterns.add(excludePattern);
    }

    public void addExcludePatterns(List<String> excludePatterns) {
	this.excludePatterns.addAll(excludePatterns);
    }

    public void setExcludePatterns(List<String> excludePatterns) {
	this.excludePatterns = excludePatterns;
    }

    public List<String> getIncludePatterns() {
	return this.includePatterns;
    }

    public void addIncludePattern(String includePattern) {
	this.includePatterns.add(includePattern);
    }

    public void addIncludePatterns(List<String> includePatterns) {
	this.includePatterns.addAll(includePatterns);
    }

    public void setIncludePatterns(List<String> includePatterns) {
	this.includePatterns = includePatterns;
    }

    
    public boolean usesTypeResolution(Language language) {
	for (Rule r : rules) {
	    if (r.getLanguage().equals(language)) {
		if (r.usesTypeResolution()) {
		    return true;
		}
	    }
	}
	return false;
    }

}
