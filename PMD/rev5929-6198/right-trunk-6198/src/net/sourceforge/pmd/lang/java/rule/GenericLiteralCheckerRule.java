
package net.sourceforge.pmd.lang.java.rule;

import java.util.regex.Pattern;

import net.sourceforge.pmd.PropertyDescriptor;
import net.sourceforge.pmd.lang.java.ast.ASTLiteral;
import net.sourceforge.pmd.lang.java.rule.regex.RegexHelper;
import net.sourceforge.pmd.lang.rule.properties.StringProperty;




public class GenericLiteralCheckerRule extends AbstractJavaRule {

	private static final String PROPERTY_NAME = "pattern";
	private static final String DESCRIPTION = "Regular Expression";
	private Pattern pattern;

	private void init() {
		if (pattern == null) {
			
			PropertyDescriptor property = new StringProperty(PROPERTY_NAME,DESCRIPTION,"", 1.0f);
			String stringPattern = super.getStringProperty(property);
			
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
