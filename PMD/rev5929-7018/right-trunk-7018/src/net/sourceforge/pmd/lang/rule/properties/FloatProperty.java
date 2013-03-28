
package net.sourceforge.pmd.lang.rule.properties;


public class FloatProperty extends AbstractNumericProperty<Float> {

	
	public FloatProperty(String theName, String theDescription,	Float min, Float max, Float theDefault, float theUIOrder) {
		super(theName, theDescription, Float.valueOf(min), Float.valueOf(max), Float.valueOf(theDefault), theUIOrder);		
	}
	
	
    public FloatProperty(String theName, String theDescription, String minStr, String maxStr, String defaultStr, float theUIOrder) {
        this(theName, theDescription, floatFrom(minStr), floatFrom(maxStr), floatFrom(defaultStr), theUIOrder);      
    }
	
    
    public static Float floatFrom(String numberString) {
        return Float.valueOf(numberString);
    }
    
	
	public Class<Float> type() {
		return Float.class;
	}

	
	protected Object createFrom(String value) {
		return floatFrom(value);
	}
}
