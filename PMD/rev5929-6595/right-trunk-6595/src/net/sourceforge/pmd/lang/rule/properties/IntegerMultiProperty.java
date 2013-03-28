
package net.sourceforge.pmd.lang.rule.properties;


public class IntegerMultiProperty extends AbstractMultiNumericProperty<Integer[]> {
	
	public IntegerMultiProperty(String theName, String theDescription, Integer min, Integer max, Integer[] theDefaults, float theUIOrder) {
		super(theName, theDescription, min, max, theDefaults, theUIOrder);
	}
	
	
	public Class<Integer[]> type() {
		return Integer[].class;
	}
	
	
	protected Object createFrom(String value) {
		return Integer.valueOf(value);
	}

	
	protected Object[] arrayFor(int size) {
		return new Integer[size];
	}
}
