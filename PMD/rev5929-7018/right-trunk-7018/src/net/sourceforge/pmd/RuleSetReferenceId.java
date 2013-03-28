
package net.sourceforge.pmd;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import net.sourceforge.pmd.util.ResourceLoader;
import net.sourceforge.pmd.util.StringUtil;


public class RuleSetReferenceId {
    private final boolean external;
    private final String ruleSetFileName;
    private final boolean allRules;
    private final String ruleName;
    private final RuleSetReferenceId externalRuleSetReferenceId;

    
    public RuleSetReferenceId(final String id) {
	this(id, null);
    }

    
    public RuleSetReferenceId(final String id, final RuleSetReferenceId externalRuleSetReferenceId) {
	if (externalRuleSetReferenceId != null && !externalRuleSetReferenceId.isExternal()) {
	    throw new IllegalArgumentException("Cannot pair with non-external <" + externalRuleSetReferenceId + ">.");
	}
	if (id != null && id.indexOf(',') >= 0) {
	    throw new IllegalArgumentException("A single RuleSetReferenceId cannot contain ',' (comma) characters: "
		    + id);
	}

	
	if (StringUtil.isEmpty(id) || isFullRuleSetName(id)) {
	    
	    external = true;
	    ruleSetFileName = id;
	    allRules = true;
	    ruleName = null;
	} else {
	    
	    final int separatorIndex = Math.max(id.lastIndexOf('/'), id.lastIndexOf('\\'));
	    if (separatorIndex >= 0 && separatorIndex != id.length() - 1) {
		final String name = id.substring(0, separatorIndex);
		external = true;
		if (isFullRuleSetName(name)) {
		    
		    ruleSetFileName = name;
		} else {
		    
		    int index = name.indexOf('-');
		    if (index >= 0) {
			
			ruleSetFileName = "rulesets/" + name.substring(0, index) + "/" + name.substring(index + 1)
				+ ".xml";
		    } else {
			
			if (name.matches("[0-9]+.*")) {
			    ruleSetFileName = "rulesets/releases/" + name + ".xml";
			} else {
			    
			    ruleSetFileName = name;
			}
		    }
		}

		
		allRules = false;
		ruleName = id.substring(separatorIndex + 1);
	    } else {
		
		int index = id.indexOf('-');
		if (index >= 0) {
		    
		    external = true;
		    ruleSetFileName = "rulesets/" + id.substring(0, index) + "/" + id.substring(index + 1) + ".xml";
		    allRules = true;
		    ruleName = null;
		} else {
		    
		    if (id.matches("[0-9]+.*")) {
			external = true;
			ruleSetFileName = "rulesets/releases/" + id + ".xml";
			allRules = true;
			ruleName = null;
		    } else {
			
			external = externalRuleSetReferenceId != null ? true : false;
			ruleSetFileName = externalRuleSetReferenceId != null ? externalRuleSetReferenceId
				.getRuleSetFileName() : null;
			allRules = false;
			ruleName = id;
		    }
		}
	    }
	}

	if (this.external && this.ruleName != null && !this.ruleName.equals(id) && externalRuleSetReferenceId != null) {
	    throw new IllegalArgumentException("Cannot pair external <" + this + "> with external <"
		    + externalRuleSetReferenceId + ">.");
	}
	this.externalRuleSetReferenceId = externalRuleSetReferenceId;
    }

    private static boolean isFullRuleSetName(String name) {
	return name.endsWith(".xml");
    }

    
    public static List<RuleSetReferenceId> parse(String referenceString) {
	List<RuleSetReferenceId> references = new ArrayList<RuleSetReferenceId>();
	if (referenceString.indexOf(',') == -1) {
	    references.add(new RuleSetReferenceId(referenceString));
	} else {
	    for (String name : referenceString.split(",")) {
		references.add(new RuleSetReferenceId(name));
	    }
	}
	return references;
    }

    
    public boolean isExternal() {
	return external;
    }

    
    public boolean isAllRules() {
	return allRules;
    }

    
    public String getRuleSetFileName() {
	return ruleSetFileName;
    }

    
    public String getRuleName() {
	return ruleName;
    }

    
    public InputStream getInputStream(ClassLoader classLoader) throws RuleSetNotFoundException {
	if (externalRuleSetReferenceId == null) {
	    InputStream in = StringUtil.isEmpty(ruleSetFileName) ? null : ResourceLoader.loadResourceAsStream(
		    ruleSetFileName, classLoader);
	    if (in == null) {
		throw new RuleSetNotFoundException(
			"Can't find resource "
				+ ruleSetFileName
				+ ".  Make sure the resource is a valid file or URL or is on the CLASSPATH.  Here's the current classpath: "
				+ System.getProperty("java.class.path"));
	    }
	    return in;
	} else {
	    return externalRuleSetReferenceId.getInputStream(classLoader);
	}
    }

    
    public String toString() {
	if (ruleSetFileName != null) {
	    if (allRules) {
		return ruleSetFileName;
	    } else {
		return ruleSetFileName + "/" + ruleName;
	    }

	} else {
	    if (allRules) {
		return "anonymous all Rule";
	    } else {
		return ruleName;
	    }
	}
    }
}
