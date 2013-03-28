
package net.sourceforge.pmd.lang.java.rule;

import java.util.regex.Pattern;

import net.sourceforge.pmd.lang.java.ast.ASTLiteral;
import net.sourceforge.pmd.lang.java.rule.regex.RegexHelper;
import net.sourceforge.pmd.lang.rule.properties.StringProperty;




public class GenericLiteralCheckerRule extends AbstractJavaRule {

	private Pattern pattern;
	
	private static final String PROPERTY_NAME = "regexPattern";
		
	private static final StringProperty REGEX_PROPERTY = new StringProperty(PROPERTY_NAME,"Regular expression","", 1.0f);
	
	public GenericLiteralCheckerRule() {
	    definePropertyDescriptor(REGEX_PROPERTY);
	}
	
	private void init() {
		if (pattern == null) {
			
			String stringPattern = super.getProperty(REGEX_PROPERTY);
			
			if ( stringPattern != null && stringPattern.length() > 0 ) {
				pattern = Pattern.compile(stringPattern);
			} else {
				throw new IllegalArgumentException("Must provide a value for the '" + PROPERTY_NAME + "' property.");
			}
		}
	}

	
	@Override
	public Object visit(ASTLiteral node, Object data) {
		init();
		String image = node.getImage();
		if ( image != null && image.length() > 0 && RegexHelper.isMatch(this.pattern,image) ) {
			addViolation(data, node);
		}
		return data;
	}
}
