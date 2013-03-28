
package net.sourceforge.pmd.lang.rule.properties;

import java.util.HashMap;
import java.util.Map;

import net.sourceforge.pmd.PropertyDescriptor;
import net.sourceforge.pmd.Rule;
import net.sourceforge.pmd.util.StringUtil;


public abstract class AbstractProperty<T> implements PropertyDescriptor<T> {

	private final String	name;
	private final String	description;
	private final T 		defaultValue;
	private final boolean 	isRequired;
	private final float		uiOrder;

	private static final char DELIMITER = '|';

	
	protected AbstractProperty(String theName, String theDescription, T theDefault, float theUIOrder) {
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

	
	public char multiValueDelimiter() {
		return DELIMITER;
	}

	
	public String name() {
		return name;
	}

	
	public String description() {
		return description;
	}

	
	public T defaultValue() {
		return defaultValue;
	}

	
	protected boolean defaultHasNullValue() {

		if (defaultValue == null) {
			return true;
		}

		if (isMultiValue() && isArray(defaultValue)) {
			Object[] defaults = (Object[])defaultValue;
			for (Object default1 : defaults) {
				if (default1 == null) { return true; }
			}
		}

		return false;
	}

	
	public boolean isMultiValue() {
		return false;
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

	
	public String asDelimitedString(T values) {
	    return asDelimitedString(values, multiValueDelimiter());
	}

	
	public String asDelimitedString(T values, char delimiter) {
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
				sb.append(delimiter);
				sb.append(asString(valueSet[i]));
			}
			return sb.toString();
			}

		return asString(values);
	}

	
	public int compareTo(PropertyDescriptor<?> otherProperty) {
		float otherOrder = otherProperty.uiOrder();
		return (int) (otherOrder - uiOrder);
	}

	
	public String errorFor(Object value) {

		String typeError = typeErrorFor(value);
		if (typeError != null) {
		    return typeError;
		}
		return isMultiValue() ?
			valuesErrorFor(value) :
			valueErrorFor(value);
	}

	
	protected String valueErrorFor(Object value) {

		if (value == null) {
			if (defaultHasNullValue()) {
				return null;
			}
			return "missing value";
		}
		return null;
	}

	
	protected String valuesErrorFor(Object value) {

		if (!isArray(value)) {
			return "multiple values expected";
		}

		Object[] values = (Object[])value;

		String err = null;
		for (Object value2 : values) {
			err = valueErrorFor(value2);
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

		if (isMultiValue()) {
			if (!isArray(value)) {
				return "Value is not an array of type: " + type();
			}

			Class<?> arrayType = value.getClass().getComponentType();
			if (arrayType == null || !arrayType.isAssignableFrom(type().getComponentType())) {
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
	    Object realValue = rule.getProperty(this);
		if (realValue == null && !isRequired()) {
		    return null;
		}
		return errorFor(realValue);
	}

	
	public Object[][] choices() {
		return null;
	}

	
	public int preferredRowCount() {
		return 1;
	}

	
	@Override
	public boolean equals(Object obj) {
	    if (this == obj) {
		return true;
	    }
	    if (obj == null) {
		return false;
	    }
	    if (obj instanceof PropertyDescriptor) {
		return name.equals(((PropertyDescriptor<?>)obj).name());
	    }
	    return false;
	}

	
	@Override
	public int hashCode() {
	    return name.hashCode();
	}

	
	@Override
	public String toString() {
	    return "[PropertyDescriptor: name=" + name() + ", type=" + type() + ", defaultValue=" + defaultValue() + "]";
	}

	
	protected abstract String defaultAsString();

	
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

	
	public Map<String, String> attributeValuesById() {

		Map<String, String> values = new HashMap<String, String>();
		addAttributesTo(values);
		return values;
	}

	
	protected void addAttributesTo(Map<String, String> attributes) {
		attributes.put("description", description);
		attributes.put("default", defaultAsString());
	}

}
