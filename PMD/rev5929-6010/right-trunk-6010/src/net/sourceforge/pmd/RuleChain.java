package net.sourceforge.pmd;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.sourceforge.pmd.lang.Language;
import net.sourceforge.pmd.lang.ast.Node;
import net.sourceforge.pmd.lang.java.rule.JavaRuleChainVisitor;
import net.sourceforge.pmd.lang.jsp.rule.JspRuleChainVisitor;
import net.sourceforge.pmd.lang.rule.RuleChainVisitor;



public class RuleChain {
    
    private final Map<Language, RuleChainVisitor> languageToRuleChainVisitor = new HashMap<Language, RuleChainVisitor>();

    
    public void add(RuleSet ruleSet) {
	Language language = ruleSet.getLanguage();
	for (Rule r : ruleSet.getRules()) {
	    add(language, r);
	}
    }

    
    public void add(Language language, Rule rule) {
	RuleChainVisitor visitor = getRuleChainVisitor(language);
	if (visitor != null) {
	    visitor.add(rule);
	}
    }

    
    public void apply(List<Node> nodes, RuleContext ctx, Language language) {
	RuleChainVisitor visitor = getRuleChainVisitor(language);
	if (visitor != null) {
	    visitor.visitAll(nodes, ctx);
	}
    }

    
    private RuleChainVisitor getRuleChainVisitor(Language language) {
	if (language == null) {
	    language = Language.JAVA;
	}
	RuleChainVisitor visitor = languageToRuleChainVisitor.get(language);
	if (visitor == null) {
	    if (Language.JAVA.equals(language)) {
		visitor = new JavaRuleChainVisitor();
	    } else if (Language.JSP.equals(language)) {
		visitor = new JspRuleChainVisitor();
	    } else {
		throw new IllegalArgumentException("Unknown language: " + language);
	    }
	    languageToRuleChainVisitor.put(language, visitor);
	}
	return visitor;
    }
}
