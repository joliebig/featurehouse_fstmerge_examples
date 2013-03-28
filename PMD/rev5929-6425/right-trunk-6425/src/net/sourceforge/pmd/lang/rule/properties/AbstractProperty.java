
package net.sourceforge.pmd.lang.rule.properties;

import net.sourceforge.pmd.PropertyDescriptor;
import net.sourceforge.pmd.Rule;
import net.sourceforge.pmd.util.StringUtil;


public abstract class AbstractProperty implements PropertyDescriptor {

	private final String	name;
	private final String	description;
	private final Object 	defaultValue;
	private final boolean 	isRequired;
	private boolean			isMultiValue = false;
	private final float		uiOrder;
	
	protected char	multiValueDelimiter = '|';
	
	
	protected AbstractProperty(String theName, String theDescription, Object theDefault, float theUIOrder) {
		name = checkNotEmpty(theName, "name");
		description = checkNotEmpty(theDescription, "description");
		defaultValue = theDefault;
		isRequired = false;	
		uiOrder = checkPositive(theUIOrder, "UI order");
	}
	
	
	private static String checkNotEmpty(String arg, String argId) {
		
		if (StringUtil.isEmpty(arg)) {
			throw new IllegalArgumentException("Property attribute '" + argId + "' cannot be null or blank");
		}
		
		return arg;
	}

	
	private static float checkPositive(float arg, String argId) {
		if (arg < 0) {
			throw new IllegalArgumentException("Property attribute " + argId + "' must be zero or positive");
		}
		return arg;
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
	
	
	protected boolean defaultHasNullValue() {
		
		if (defaultValue == null) {
			return true;
		}
		
		if (isMultiValue && isArray(defaultValue)) {
			Object[] defaults = (Object[])defaultValue;
			for (int i=0; i<defaults.length; i++) {
				if (defaults[i] == null) { return true; }
			}
		} 
		
		return false;
	}
	
	
	public boolean isMultiValue() {
		return isMultiValue;
	}
	
	
	protected void isMultiValue(boolean flag) {
		isMultiValue = flag;
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
		
		if (values == null) {
		    return "";
		}
		
		if (values instanceof Object[]) {
			Object[] valueSet = (Object[])values;
			if (valueSet.length == 0) {
			    return "";
			}
			if (valueSet.length == 1) {
			    return asString(valueSet[0]);
			}
			
			StringBuilder sb = new StringBuilder();
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
		if (typeError != null) {
		    return typeError;
		}
		return isMultiValue ?
			valuesErrorFor(value) :
			valueErrorFor(value);
	}
	
	
	protected String valueErrorFor(Object value) {
		
		if (value == null) {
			if (defaultHasNullValue()) {
				return null;
			} else {
				return "missing value";
				}
		}
		return null;
	}
	
	
	protected String valuesErrorFor(Object value) {
		
		if (!isArray(value)) {
			return "multiple values expected";
		}
		
		Object[] values = (Object[])value;
		
		String err = null;
		for (int i=0; i<values.length; i++) {
			err = valueErrorFor(values[i]);
			if (err != null) { return err; }
		}
		
		return null;
	}
	
	
	protected static boolean isArray(Object value) {
		return value != null && value.getClass().getComponentType() != null;
	}
	
	
	protected String typeErrorFor(Object value) {
		
		if (value == null && !isRequired) {
		    return null;
		}
		
		if (isMultiValue) {
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
		String strValue = rule.getStringProperty(this);
		if (strValue == null && !isRequired()) {
		    return null;
		}
		Object realValue = valueFrom(strValue);
		return errorFor(realValue);
	}
	
	
	public Object[][] choices() {
		return null;
	}
	
	
	public int preferredRowCount() {
		return 1;
	}
	
	
	@SuppressWarnings("PMD.CompareObjectsWithEquals")
	public static final boolean areEqual(Object value, Object otherValue) {
		if (value == otherValue) {
		    return true;
		}
		if (value == null) {
		    return false;
		}
		if (otherValue == null) {
		    return false;
		}

		return value.equals(otherValue);
	}
}
