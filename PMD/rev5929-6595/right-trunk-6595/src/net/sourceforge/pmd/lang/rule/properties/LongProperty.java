
package net.sourceforge.pmd.lang.rule.properties;


public class LongProperty extends AbstractNumericProperty<Long> {

	
	public LongProperty(String theName, String theDescription, Long min, Long max, Long theDefault, float theUIOrder) {
		super(theName, theDescription, min, max, theDefault, theUIOrder);		
	}
	
	
    public LongProperty(String theName, String theDescription, String minStr, String maxStr, String defaultStr, float theUIOrder) {
        this(theName, theDescription, longFrom(minStr), longFrom(maxStr), longFrom(defaultStr), theUIOrder);       
    }
	
    
    public static Long longFrom(String numberString) {
        return Long.valueOf(numberString);
    }
    
	
	public Class<Long> type() {
		return Long.class;
	}

	
	protected Object createFrom(String value) {
		return longFrom(value);
	}
}
