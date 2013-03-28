
package net.sourceforge.pmd.lang.rule.properties;


public class FloatProperty extends AbstractScalarProperty {

	
	public FloatProperty(String theName, String theDescription,	float theDefault, float theUIOrder) {
		super(theName, theDescription, new Float(theDefault), theUIOrder);
	}

	
	public FloatProperty(String theName, String theDescription, float[] defaultValues, float theUIOrder, int theMaxValues) {
		this(theName, theDescription, asFloats(defaultValues), theUIOrder, theMaxValues);		
	}
	
	
	public FloatProperty(String theName, String theDescription, Float[] defaultValues, float theUIOrder, int theMaxValues) {
		super(theName, theDescription, defaultValues, theUIOrder);
		
		maxValueCount(theMaxValues);
	}
	
	
	public Class<Float> type() {
		return Float.class;
	}

	
	private static final Float[] asFloats(float[] floats) {
		Float[] Floats = new Float[floats.length];
		for (int i=0; i<floats.length; i++) {
		    Floats[i] = new Float(floats[i]);
		}
		return Floats;
	}

	
	protected Object createFrom(String value) {
		return Float.valueOf(value);
	}

	
	protected Object[] arrayFor(int size) {
		return new Float[size];
	}
}
