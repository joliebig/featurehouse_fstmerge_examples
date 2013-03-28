
package net.sourceforge.pmd.lang.rule.properties;


public class IntegerProperty extends AbstractScalarProperty {

	
	public IntegerProperty(String theName, String theDescription, int theDefault, float theUIOrder) {
		super(theName, theDescription, theDefault, theUIOrder);
	}

	
	public IntegerProperty(String theName, String theDescription, int[] theDefaults, float theUIOrder, int maxCount) {
		this(theName, theDescription, asIntegers(theDefaults), theUIOrder, maxCount);
	}
	
	
	public IntegerProperty(String theName, String theDescription, Integer[] theDefaults, float theUIOrder, int maxCount) {
		super(theName, theDescription, theDefaults, theUIOrder);
		
		maxValueCount(maxCount);
	}
	
	
	private static final Integer[] asIntegers(int[] ints) {
		Integer[] integers = new Integer[ints.length];
		for (int i=0; i<ints.length; i++) {
		    integers[i] = Integer.valueOf(ints[i]);
		}
		return integers;
	}
	
	
	public Class<Integer> type() {
		return Integer.class;
	}

	
	protected Object createFrom(String value) {
		return Integer.valueOf(value);
	}

	
	protected Object[] arrayFor(int size) {
		return new Integer[size];
	}
}
