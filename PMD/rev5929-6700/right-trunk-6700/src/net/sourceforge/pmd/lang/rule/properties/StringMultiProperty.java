
package net.sourceforge.pmd.lang.rule.properties;

import java.util.Map;

import net.sourceforge.pmd.util.StringUtil;


public class StringMultiProperty extends AbstractDelimitedProperty<String[]> {
		
	public static final char DEFAULT_DELIMITER = '|';
		
	
	public StringMultiProperty(String theName, String theDescription, String[] theDefaults, float theUIOrder, char delimiter) {
		super(theName, theDescription, theDefaults, delimiter, theUIOrder);

		checkDefaults(theDefaults, delimiter);
	}
	
	
	public StringMultiProperty(String theName, String theDescription, String theDefaults, Map<String, String> otherParams) {
	    this(theName, theDescription, StringUtil.substringsOf(theDefaults, delimiterIn(otherParams)), 0.0f, delimiterIn(otherParams));
	}
	
	
	private static void checkDefaults(String[] defaultValue, char delim) {
		
		if (defaultValue == null) { return;	}
		
		for (int i=0; i<defaultValue.length; i++) {
			if (defaultValue[i].indexOf(delim) >= 0) {
				throw new IllegalArgumentException("Cannot include the delimiter in the set of defaults");
			}
		}
	}
	
	
	public Class<String[]> type() {
		return String[].class;
	}
	
	
	public String[] valueFrom(String valueString) {
		return StringUtil.substringsOf(valueString, multiValueDelimiter());
	}
	
	
	private boolean containsDelimiter(String value) {
		return value.indexOf(multiValueDelimiter()) >= 0;
	}
	
	
	private final String illegalCharMsg() {
		return "Value cannot contain the '" + multiValueDelimiter() + "' character";
	}
	
	
	protected String valueErrorFor(Object value) {

		if (value==null) { return "missing value"; }
		
		String testValue = (String)value;
		if (containsDelimiter(testValue)) {
		    return illegalCharMsg();			
		}
		
		
		
		return null;		
	}
}
