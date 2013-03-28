
package net.sourceforge.pmd.lang.rule.properties;


public class IntegerProperty extends AbstractNumericProperty {

	
	public IntegerProperty(String theName, String theDescription, int min, int max, int theDefault, float theUIOrder) {
		super(theName, theDescription, Integer.valueOf(min), Integer.valueOf(max), theDefault, theUIOrder);
		
		isMultiValue(false);
	}

	
	public IntegerProperty(String theName, String theDescription, int min, int max, int[] theDefaults, float theUIOrder) {
		this(theName, theDescription, Integer.valueOf(min), Integer.valueOf(max), asIntegers(theDefaults), theUIOrder);
	}
	
	
	public IntegerProperty(String theName, String theDescription, Integer min, Integer max, Integer[] theDefaults, float theUIOrder) {
		super(theName, theDescription, min, max, theDefaults, theUIOrder);
		
		isMultiValue(true);
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
