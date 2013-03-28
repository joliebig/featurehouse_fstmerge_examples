package net.sourceforge.pmd.lang.rule.properties;

import java.util.Map;

import net.sourceforge.pmd.NumericPropertyDescriptor;
import net.sourceforge.pmd.lang.rule.properties.factories.BasicPropertyDescriptorFactory;


public abstract class AbstractNumericProperty<T> extends AbstractScalarProperty<T> implements NumericPropertyDescriptor<T> {

	private Number lowerLimit;
	private Number upperLimit;
	
	public static final Map<String, Boolean> numberFieldTypesByKey = BasicPropertyDescriptorFactory.expectedFieldTypesWith(
			new String[]  { minKey,  	  maxKey}, 
			new Boolean[] { Boolean.TRUE, Boolean.TRUE}
			);
	
	
	protected AbstractNumericProperty(String theName, String theDescription, Number lower, Number upper, T theDefault, float theUIOrder) {
		super(theName, theDescription, theDefault, theUIOrder);
	
		if (lower.doubleValue() > upper.doubleValue()) {
			throw new IllegalArgumentException("Lower limit cannot be greater than the upper limit");
		}
		
		lowerLimit = lower;
		upperLimit = upper;
	}
	
	
	public Number lowerLimit() {
		return lowerLimit;
	}
	
    
    protected String defaultAsString() {
        return defaultValue().toString();
    }
	
	
	public Number upperLimit() {
		return upperLimit;
	}
	
	
	public String rangeString() {
		StringBuilder sb = new StringBuilder();
		sb.append('(').append(lowerLimit);
		sb.append(" -> ").append(upperLimit);
		sb.append(')');
		return sb.toString();
	}
	
	
	protected String valueErrorFor(Object value) {
		
		double number = ((Number)value).doubleValue();
		
		if (number > upperLimit.doubleValue() || number < lowerLimit.doubleValue() ) {
			return value + " is out of range " + rangeString();
		}
		
		return null;
	}
	
	
	protected void addAttributesTo(Map<String, String> attributes) {
		super.addAttributesTo(attributes);
		
		attributes.put(minKey, lowerLimit.toString());
		attributes.put(maxKey, upperLimit.toString());
	}
}
