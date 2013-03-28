
package net.sourceforge.pmd.lang.rule.properties;


public class FloatMultiProperty extends AbstractMultiNumericProperty<Float[]> {
	
	public FloatMultiProperty(String theName, String theDescription, Float min, Float max, Float[] defaultValues, float theUIOrder) {
		super(theName, theDescription, min, max, defaultValues, theUIOrder);
	}
	
	
	public Class<Float[]> type() {
		return Float[].class;
	}
	
	
	protected Object createFrom(String value) {
		return Float.valueOf(value);
	}

	
	protected Object[] arrayFor(int size) {
		return new Float[size];
	}
}
