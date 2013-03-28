
package net.sourceforge.pmd.lang.rule.properties;



public class DoubleProperty extends AbstractNumericProperty<Double> {

	
	public DoubleProperty(String theName, String theDescription, Double min, Double max, Double theDefault, float theUIOrder) {
		super(theName, theDescription, min, max, theDefault, theUIOrder);		
	}
	
	
    public DoubleProperty(String theName, String theDescription, String minStr, String maxStr, String defaultStr, float theUIOrder) {
        this(theName, theDescription, doubleFrom(minStr), doubleFrom(maxStr), doubleFrom(defaultStr), theUIOrder);       
    }
	
    
    
    public static Double doubleFrom(String numberString) {
        return Double.valueOf(numberString);
    }
    
	
	public Class<Double> type() {
		return Double.class;
	}

	
	protected Object createFrom(String value) {
		return doubleFrom(value);
	}
}
