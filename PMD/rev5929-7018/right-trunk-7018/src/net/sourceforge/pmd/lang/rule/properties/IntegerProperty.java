
package net.sourceforge.pmd.lang.rule.properties;


public class IntegerProperty extends AbstractNumericProperty<Integer> {

	
	public IntegerProperty(String theName, String theDescription, Integer min, Integer max, Integer theDefault, float theUIOrder) {
		super(theName, theDescription, min, max, theDefault, theUIOrder);		
	}
	
	
	public IntegerProperty(String theName, String theDescription, String minStr, String maxStr, String defaultStr, float theUIOrder) {
        this(theName, theDescription, intFrom(minStr), intFrom(maxStr), intFrom(defaultStr), theUIOrder);       
    }
	
	
	public static Integer intFrom(String numberString) {
	    return Integer.valueOf(numberString);
	}
	
	
	public Class<Integer> type() {
		return Integer.class;
	}

	
	protected Object createFrom(String value) {
		return intFrom(value);
	}
}
