
package net.sourceforge.pmd.lang.rule.properties;


public class BooleanProperty extends AbstractScalarProperty {

	
	public BooleanProperty(String theName, String theDescription, boolean defaultValue, float theUIOrder) {
		super(theName, theDescription, Boolean.valueOf(defaultValue), theUIOrder);
	}

	
	public BooleanProperty(String theName, String theDescription, boolean[] defaultValues, float theUIOrder, int theMaxValues) {
		this(theName, theDescription, asBooleans(defaultValues), theUIOrder, theMaxValues);
		
	}
	
	
	public BooleanProperty(String theName, String theDescription, Boolean[] defaultValues, float theUIOrder, int theMaxValues) {
		super(theName, theDescription, defaultValues, theUIOrder);
		
		maxValueCount(theMaxValues);
	}
	
	
	private static final Boolean[] asBooleans(boolean[] bools) {
		Boolean[] booleans = new Boolean[bools.length];
		for (int i=0; i<bools.length; i++) booleans[i] = Boolean.valueOf(bools[i]);
		return booleans;
	}
	
	
	public Class<Boolean> type() {
		return Boolean.class;
	}

	
	protected Object createFrom(String value) {
		return Boolean.valueOf(value);
	}

	
	protected Object[] arrayFor(int size) {
		return new Boolean[size];
	}
}
