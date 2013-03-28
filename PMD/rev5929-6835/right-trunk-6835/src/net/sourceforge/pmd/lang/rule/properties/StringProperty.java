
package net.sourceforge.pmd.lang.rule.properties;



public class StringProperty extends AbstractProperty<String> {
	
	public StringProperty(String theName, String theDescription, String theDefaultValue, float theUIOrder) {
		super(theName, theDescription, theDefaultValue, theUIOrder);
	}
	
    
    protected String defaultAsString() {
        return defaultValue();
    }
	
	
	public Class<String> type() {
		return String.class;
	}
	
	
	public String valueFrom(String valueString) {
		return valueString;
	}
}
