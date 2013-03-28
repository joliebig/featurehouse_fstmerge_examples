
package net.sourceforge.pmd.lang.rule.properties;


public class DoubleProperty extends AbstractScalarProperty {

	
	public DoubleProperty(String theName, String theDescription, double theDefault, float theUIOrder) {
		super(theName, theDescription, new Double(theDefault), theUIOrder);
	}

	
	public DoubleProperty(String theName, String theDescription, double[] defaultValues, float theUIOrder, int theMaxValues) {
		this(theName, theDescription, asDoubles(defaultValues), theUIOrder, theMaxValues);		
	}
	
	
	public DoubleProperty(String theName, String theDescription, Double[] defaultValues, float theUIOrder, int theMaxValues) {
		super(theName, theDescription, defaultValues, theUIOrder);
		
		maxValueCount(theMaxValues);
	}
	
	
	public Class<Double> type() {
		return Double.class;
	}

	
	private static final Double[] asDoubles(double[] doubles) {
		Double[] Doubles = new Double[doubles.length];
		for (int i=0; i<doubles.length; i++) {
		    Doubles[i] = new Double(doubles[i]);
		}
		return Doubles;
	}

	
	protected Object createFrom(String value) {
		return Double.valueOf(value);
	}

	
	protected Object[] arrayFor(int size) {
		return new Double[size];
	}
}
