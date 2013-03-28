package net.sourceforge.pmd;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.sourceforge.pmd.ast.CompilationUnit;
import net.sourceforge.pmd.ast.JavaRuleChainVisitor;
import net.sourceforge.pmd.jsp.ast.JspRuleChainVisitor;


public class RuleChain {
    
    private final Map<Language, RuleChainVisitor> languageToRuleChainVisitor = new HashMap<Language, RuleChainVisitor>();

    
    public void add(RuleSet ruleSet) {
        Language language = ruleSet.getLanguage();
        for (Rule r: ruleSet.getRules()) {
            add(ruleSet, r, language);
        }
    }

    
    private void add(RuleSet ruleSet, Rule rule, Language language) {
        RuleChainVisitor visitor = getRuleChainVisitor(language);
        if (visitor != null) {
            visitor.add(ruleSet, rule);
        }
    }

    
    public void apply(List<CompilationUnit> astCompilationUnits, RuleContext ctx,
            Language language) {
        RuleChainVisitor visitor = getRuleChainVisitor(language);
        if (visitor != null) {
            visitor.visitAll(astCompilationUnits, ctx);
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
                throw new IllegalArgumentException("Unknown language: "
                        + language);
            }
            languageToRuleChainVisitor.put(language, visitor);
        }
        return visitor;
    }
}
