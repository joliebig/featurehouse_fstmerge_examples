
package net.sourceforge.pmd.lang.rule.properties;



public class DoubleProperty extends AbstractNumericProperty {

	
	public DoubleProperty(String theName, String theDescription, double min, double max, double theDefault, float theUIOrder) {
		super(theName, theDescription, Double.valueOf(min), Double.valueOf(max), Double.valueOf(theDefault), theUIOrder);
		
		isMultiValue(false);
	}

	
	public DoubleProperty(String theName, String theDescription, double min, double max, double[] defaultValues, float theUIOrder) {
		this(theName, theDescription, Double.valueOf(min), Double.valueOf(max), asDoubles(defaultValues), theUIOrder);		
	}
	
	
	public DoubleProperty(String theName, String theDescription, Double min, Double max, Double[] defaultValues, float theUIOrder) {
		super(theName, theDescription, min, max, defaultValues, theUIOrder);
		
		isMultiValue(true);
	}
	
	
	public Class<Double> type() {
		return Double.class;
	}

	
	private static final Double[] asDoubles(double[] d) {
		Double[] doubles = new Double[d.length];
		for (int i=0; i<d.length; i++) {
		    doubles[i] = Double.valueOf(d[i]);
		}
		return doubles;
	}

	
	protected Object createFrom(String value) {
		return Double.valueOf(value);
	}

	
	protected Object[] arrayFor(int size) {
		return new Double[size];
	}
}
