
package net.sf.jabref.groups;

import java.util.Map;

import net.sf.jabref.BibtexEntry;
import net.sf.jabref.SearchRule;
import net.sf.jabref.SearchRuleSet;


class AndOrSearchRuleSet extends SearchRuleSet {

    private boolean and, invert;

    public AndOrSearchRuleSet(boolean and, boolean invert) {
        this.and = and;
        this.invert = invert;
    }

    public int applyRule(Map<String, String> searchString, BibtexEntry bibtexEntry) {
        int score = 0;
        
        
        for (SearchRule rule : ruleSet) {
			score += rule.applyRule(searchString, bibtexEntry) > 0 ? 1 : 0;
		}

        
        
        boolean res;
        if (and)
            res = (score == ruleSet.size());
        else
            res = (score > 0);

        if (invert)
            return (res ? 0 : 1);
        return (res ? 1 : 0);
    }
}
