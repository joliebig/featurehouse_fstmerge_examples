package net.sourceforge.pmd.properties;

import net.sourceforge.pmd.PropertyDescriptor;
import net.sourceforge.pmd.Rule;



public abstract class AbstractPMDProperty implements PropertyDescriptor {

	private String	name;
	private String	description;
	private Object 	defaultValue;
	private boolean isRequired = false;
	private int		maxValueCount = 1;
	private float	uiOrder;
	
	protected char	multiValueDelimiter = '|';
	
	
	protected AbstractPMDProperty(String theName, String theDescription, Object theDefault, float theUIOrder) {
		name = theName;
		description = theDescription;
		defaultValue = theDefault;
		uiOrder = theUIOrder;
	}
	
	
	protected void multiValueDelimiter(char aDelimiter) {
		multiValueDelimiter = aDelimiter;
	}
	
	
	public char multiValueDelimiter() {
		return multiValueDelimiter;
	}
	
	
	public String name() {
		return name;
	}

	
	public String description() {
		return description;
	}

	
	public Object defaultValue() {
		return defaultValue;
	}
	
	
	public int maxValueCount() {
		return maxValueCount;
	}
	
	
	protected void maxValueCount(int theCount) {
		maxValueCount = theCount;
	}
	
	
	public boolean isRequired() {
		return isRequired;
	}
	
	
	public float uiOrder() {
		return uiOrder;
	}
	
	
	protected String asString(Object value) {
		return value == null ? "" : value.toString();
	}
	
	
	
	public String asDelimitedString(Object values) {
		
		if (values == null) return "";
		
		if (values instanceof Object[]) {
			Object[] valueSet = (Object[])values;
			if (valueSet.length == 0) return "";
			if (valueSet.length == 1) return asString(valueSet[0]);
			
			StringBuffer sb = new StringBuffer();
			sb.append(asString(valueSet[0]));
			for (int i=1; i<valueSet.length; i++) {
				sb.append(multiValueDelimiter);
				sb.append(asString(valueSet[i]));
			}
			return sb.toString();
			}

		return asString(values);
	}
	
	
	public int compareTo(PropertyDescriptor otherProperty) {
		float otherOrder = otherProperty.uiOrder();
		return (int) (otherOrder - uiOrder);
	}
	
	
	public String errorFor(Object value) {
		
		String typeError = typeErrorFor(value);
		if (typeError != null) return typeError;
		return valueErrorFor(value);
	}
	
	
	protected String valueErrorFor(Object value) {
		
		return null;
	}
	
	
	protected boolean isArray(Object value) {
		return value != null && value.getClass().getComponentType() != null;
	}
	
	
	protected String typeErrorFor(Object value) {
		
		if (value == null && !isRequired) return null;
		
		if (maxValueCount > 1) {
			if (!isArray(value)) {
				return "Value is not an array of type: " + type();
			}
			
			Class<?> arrayType = value.getClass().getComponentType();
			if (arrayType == null || !arrayType.isAssignableFrom(type())) {
				return "Value is not an array of type: " + type();
			}
			return null;
		}
		
		if (!type().isAssignableFrom(value.getClass())) {
			return value + " is not an instance of " + type();
		}

		return null;
	}
	
	
	public String propertyErrorFor(Rule rule) {
		String strValue = rule.getStringProperty(name());
		if (strValue == null && !isRequired()) return null;
		Object realValue = valueFrom(strValue);
		return errorFor(realValue);
	}
	
	
	public Object[][] choices() {
		return null;
	}
	
	
	public int preferredRowCount() {
		return 1;
	}
	
	
	public static final boolean areEqual(Object value, Object otherValue) {
		if (value == otherValue) return true;
		if (value == null) return false;
		if (otherValue == null) return false;

		return value.equals(otherValue);
	}
}
