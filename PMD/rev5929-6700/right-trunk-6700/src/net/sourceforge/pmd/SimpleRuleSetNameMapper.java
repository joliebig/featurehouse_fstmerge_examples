package net.sourceforge.pmd;

import java.util.logging.Logger;


public final class SimpleRuleSetNameMapper {

    private static final Logger LOG = Logger.getLogger(SimpleRuleSetNameMapper.class.getName());

    @SuppressWarnings("PMD.AvoidStringBufferField")
    private final StringBuilder rulesets = new StringBuilder();

    public SimpleRuleSetNameMapper(String ruleString) {
	if (ruleString.indexOf(',') == -1) {
	    check(ruleString);
	    return;
	}
	for (String name : ruleString.split(",")) {
	    check(name);
	}
    }

    public String getRuleSets() {
	return rulesets.toString();
    }

    protected void check(String name) {
	
	final String resourceName;
	if (name.indexOf("rulesets") == -1) {
	    
	    if (name.indexOf('/') < 0 && name.indexOf('\\') < 0 && name.indexOf('.') < 0) {
		
		int index = name.indexOf('-');
		if (index >= 0) {
		    
		    resourceName = "rulesets/" + name.substring(0, index) + "/" + name.substring(index + 1) + ".xml";
		} else {
		    
		    if (name.matches("[0-9]+.*")) {
			resourceName = "rulesets/" + name + ".xml";
		    } else {
			
			resourceName = name;
		    }
		}
	    } else {
		
		resourceName = name;
	    }
	} else {
	    
	    resourceName = name;
	}

	append(resourceName);
    }

    protected void append(String name) {
	if (rulesets.length() > 0) {
	    rulesets.append(',');
	}
	rulesets.append(name);
    }
}
