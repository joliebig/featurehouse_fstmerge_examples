package net.sourceforge.pmd.properties;

import net.sourceforge.pmd.util.StringUtil;


public class StringProperty extends AbstractPMDProperty {
		
	private int preferredRowCount;
	
	public static final char defaultDelimiter = '|';
	
	
	public StringProperty(String theName, String theDescription, String theDefaultValue, float theUIOrder) {
		this(theName, theDescription, theDefaultValue, theUIOrder, defaultDelimiter);
		
		maxValueCount(1);
	}
		
	
	public StringProperty(String theName, String theDescription, String[] theValues, float theUIOrder, char aMultiValueDelimiter) {
		super(theName, theDescription, theValues, theUIOrder);
		
		maxValueCount(Integer.MAX_VALUE);
		multiValueDelimiter(aMultiValueDelimiter);
	}
	
	
	protected StringProperty(String theName, String theDescription, Object theDefaultValue, float theUIOrder, char aMultiValueDelimiter) {
		super(theName, theDescription, theDefaultValue, theUIOrder);
		
		maxValueCount(Integer.MAX_VALUE);
		multiValueDelimiter(aMultiValueDelimiter);
	}
	
	
	public Class<?> type() {
		return String.class;
	}
	
	
	public Object valueFrom(String valueString) {
		
		if (maxValueCount() == 1) return valueString;
		
		return StringUtil.substringsOf(valueString, multiValueDelimiter);
	}
	
	
	private boolean containsDelimiter(String value) {
		return value.indexOf(multiValueDelimiter) >= 0;
	}
	
	private final String illegalCharMsg() {
		return "Value cannot contain the \"" + multiValueDelimiter + "\" character";
	}
	
	
	protected String valueErrorFor(Object value) {

		if (maxValueCount() == 1) {
			String testValue = (String)value;
			if (!containsDelimiter(testValue)) return null;			
			return illegalCharMsg();
		}
		
		String[] values = (String[])value;
		for (int i=0; i<values.length; i++) {
			if (!containsDelimiter(values[i])) continue;	
			return illegalCharMsg();
			}
		
		return null;
	}
	
	public int preferredRowCount() {
		return preferredRowCount;
	}
}
