
package net.sourceforge.pmd.lang.rule.properties;



public class DoubleMultiProperty extends AbstractMultiNumericProperty<Double[]> {
	
	public DoubleMultiProperty(String theName, String theDescription, Double min, Double max, Double[] defaultValues, float theUIOrder) {
		super(theName, theDescription, min, max, defaultValues, theUIOrder);
	}
	
	
	public Class<Double[]> type() {
		return Double[].class;
	}

	
	protected Object createFrom(String value) {
		return Double.valueOf(value);
	}

	
	protected Object[] arrayFor(int size) {
		return new Double[size];
	}
}
