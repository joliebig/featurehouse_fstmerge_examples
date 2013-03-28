package net.sourceforge.pmd;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;


public interface PropertyDescriptor extends Comparable<PropertyDescriptor> {

	PropertyDescriptor[] EMPTY_PROPERTY_SET = new PropertyDescriptor[0];

	Map<PropertyDescriptor, Object> EMPTY_VALUE_MAP = Collections
			.unmodifiableMap(new HashMap<PropertyDescriptor, Object>(0));

	
	String name();

	
	String description();

	
	Class<?> type();

	
	boolean isMultiValue();

	
	Object defaultValue();

	
	boolean isRequired();

	
	String errorFor(Object value);

	
	float uiOrder();

	
	Object valueFrom(String propertyString) throws IllegalArgumentException;

	
	String asDelimitedString(Object value);

	
	Object[][] choices();

	
	String propertyErrorFor(Rule rule);

	
	char multiValueDelimiter();

	
	int preferredRowCount();
}
