
package net.sourceforge.pmd.lang.rule.properties;


public class FloatProperty extends AbstractNumericProperty {

	
	public FloatProperty(String theName, String theDescription,	float min, float max, float theDefault, float theUIOrder) {
		super(theName, theDescription, Float.valueOf(min), Float.valueOf(max), Float.valueOf(theDefault), theUIOrder);
		
		isMultiValue(false);
	}

	
	public FloatProperty(String theName, String theDescription, float min, float max, float[] defaultValues, float theUIOrder) {
		this(theName, theDescription, Float.valueOf(min), Float.valueOf(max), asFloats(defaultValues), theUIOrder);		
	}
	
	
	public FloatProperty(String theName, String theDescription, Float min, Float max, Float[] defaultValues, float theUIOrder) {
		super(theName, theDescription, min, max, defaultValues, theUIOrder);
		
		isMultiValue(true);
	}
	
	
	public Class<Float> type() {
		return Float.class;
	}

	
	private static final Float[] asFloats(float[] f) {
		Float[] floats = new Float[f.length];
		for (int i=0; i<f.length; i++) {
		    floats[i] = Float.valueOf(f[i]);
		}
		return floats;
	}

	
	protected Object createFrom(String value) {
		return Float.valueOf(value);
	}

	
	protected Object[] arrayFor(int size) {
		return new Float[size];
	}
}
