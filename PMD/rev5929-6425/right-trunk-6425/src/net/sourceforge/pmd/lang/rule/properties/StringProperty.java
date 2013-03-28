
package net.sourceforge.pmd.lang.rule.properties;

import net.sourceforge.pmd.util.StringUtil;


public class StringProperty extends AbstractProperty {
		
	private int preferredRowCount;
	
	public static final char DEFAULT_DELIMITER = '|';
	
	
	public StringProperty(String theName, String theDescription, String theDefaultValue, float theUIOrder) {
		this(theName, theDescription, theDefaultValue, theUIOrder, DEFAULT_DELIMITER);
	}
		
	
	public StringProperty(String theName, String theDescription, String[] theDefaults, float theUIOrder, char aMultiValueDelimiter) {
		super(theName, theDescription, theDefaults, theUIOrder);
			
		isMultiValue(true);
		multiValueDelimiter(aMultiValueDelimiter);

		checkDefaults(theDefaults, aMultiValueDelimiter);
	}
	
	
	protected StringProperty(String theName, String theDescription, Object theDefaultValue, float theUIOrder, char aMultiValueDelimiter) {
		super(theName, theDescription, theDefaultValue, theUIOrder);
				
		isMultiValue(isArray(theDefaultValue));
		multiValueDelimiter(aMultiValueDelimiter);
		
		checkDefaults(theDefaultValue, aMultiValueDelimiter);
	}
	
	
	private static void checkDefaults(Object defaultValue, char delim) {
		
		if (defaultValue == null) { return;	}
		
		if (isArray(defaultValue) && defaultValue instanceof String[]) {
			String[] defaults = (String[])defaultValue;
			for (int i=0; i<defaults.length; i++) {
				if (defaults[i].indexOf(delim) >= 0) {
					throw new IllegalArgumentException("Cannot include the delimiter in the set of defaults");
				}
			}
		}
	}
	
	
	public Class<?> type() {
		return String.class;
	}
	
	
	public Object valueFrom(String valueString) {
		
		if (isMultiValue()) {
		    return StringUtil.substringsOf(valueString, multiValueDelimiter);
			}
		
		return valueString;
	}
	
	
	private boolean containsDelimiter(String value) {
		return value.indexOf(multiValueDelimiter) >= 0;
	}
	
	
	private final String illegalCharMsg() {
		return "Value cannot contain the '" + multiValueDelimiter + "' character";
	}
	
	
	protected String valueErrorFor(Object value) {

		if (value==null) { return "missing value"; }
		
		String testValue = (String)value;
		if (isMultiValue() && containsDelimiter(testValue)) {
		    return illegalCharMsg();			
		}
		
		
		
		return null;		
	}
	
	
	public int preferredRowCount() {
		return preferredRowCount;
	}
}
