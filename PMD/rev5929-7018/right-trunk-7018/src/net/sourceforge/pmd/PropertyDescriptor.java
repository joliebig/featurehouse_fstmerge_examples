package net.sourceforge.pmd;

import java.util.Map;


public interface PropertyDescriptor<T extends Object> extends Comparable<PropertyDescriptor<?>> {
	
	String name();

	
	String description();

	
	Class<T> type();

	
	boolean isMultiValue();

	
	T defaultValue();

	
	boolean isRequired();

	
	String errorFor(Object value);

	
	float uiOrder();

	
	T valueFrom(String propertyString) throws IllegalArgumentException;

	
	String asDelimitedString(T value);

	
	Object[][] choices();

	
	String propertyErrorFor(Rule rule);

	
	char multiValueDelimiter();

	
	int preferredRowCount();
	
	
	Map<String, String> attributeValuesById();
}
