
package net.sourceforge.pmd.lang.java.rule;

import java.util.Map;
import java.util.regex.Pattern;

import net.sourceforge.pmd.PropertyDescriptor;
import net.sourceforge.pmd.lang.java.ast.ASTLiteral;
import net.sourceforge.pmd.lang.java.rule.regex.RegexHelper;
import net.sourceforge.pmd.lang.rule.properties.StringProperty;




public class GenericLiteralCheckerRule extends AbstractJavaRule {

	private Pattern pattern;
	
	private static final String PROPERTY_NAME = "regexPattern";
		
	private static final PropertyDescriptor REGEX_PROPERTY = new StringProperty(PROPERTY_NAME,"Regular expression","", 1.0f);

    private static final Map<String, PropertyDescriptor> PROPERTY_DESCRIPTORS_BY_NAME = asFixedMap(REGEX_PROPERTY);

    protected Map<String, PropertyDescriptor> propertiesByName() {
        return PROPERTY_DESCRIPTORS_BY_NAME;
    }
	
	private void init() {
		if (pattern == null) {
			
			String stringPattern = super.getStringProperty(REGEX_PROPERTY);
			
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
