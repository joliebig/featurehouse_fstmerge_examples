package net.sourceforge.pmd;



public interface PropertyDescriptor extends Comparable<PropertyDescriptor> {

	PropertyDescriptor[] emptyPropertySet = new PropertyDescriptor[0];
	
	
	String name();
	
	String description();
	
	Class<?> type();
	
	int maxValueCount();
	
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
