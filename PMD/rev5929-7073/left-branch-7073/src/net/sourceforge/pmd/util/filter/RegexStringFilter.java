
package net.sourceforge.pmd.util.filter;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;


public class RegexStringFilter implements Filter<String> {
    
    private static final Pattern ENDS_WITH = Pattern
	    .compile("\\^?\\.\\*([^\\\\\\[\\(\\.\\*\\?\\+\\|\\{\\$]+)(?:\\\\?(\\.\\w+))?\\$?");

    protected String regex;
    protected Pattern pattern;
    protected String endsWith;

    public RegexStringFilter(String regex) {
	this.regex = regex;
	optimize();
    }

    public String getRegex() {
	return this.regex;
    }

    public String getEndsWith() {
	return this.endsWith;
    }

    protected void optimize() {
	final Matcher matcher = ENDS_WITH.matcher(this.regex);
	if (matcher.matches()) {
	    final String literalPath = matcher.group(1);
	    final String fileExtension = matcher.group(2);
	    if (fileExtension != null) {
		this.endsWith = literalPath + fileExtension;
	    } else {
		this.endsWith = literalPath;
	    }
	} else {
	    try {
		this.pattern = Pattern.compile(this.regex);
	    } catch (PatternSyntaxException e) {
		
	    }
	}
    }

    public boolean filter(String obj) {
	if (this.endsWith != null) {
	    return obj.endsWith(this.endsWith);
	} else if (this.pattern != null) {
	    return this.pattern.matcher(obj).matches();
	} else {
	    
	    return false;
	}
    }

    @Override
    public String toString() {
	return "matches " + this.regex;
    }
}