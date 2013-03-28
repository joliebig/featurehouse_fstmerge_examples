
package net.sourceforge.pmd.lang.rule.properties;



public class CharacterProperty extends AbstractProperty<Character> {

	
	public CharacterProperty(String theName, String theDescription, Character theDefault, float theUIOrder) {
		super(theName, theDescription, theDefault, theUIOrder);
	}
	
	
    public CharacterProperty(String theName, String theDescription, String defaultStr, float theUIOrder) {
        this(theName, theDescription, charFrom(defaultStr), theUIOrder);
    }
	
    
    public static Character charFrom(String charStr) {
        
        if (charStr == null || charStr.length() != 1) {
            throw new IllegalArgumentException("missing/invalid character value");
        }
        return charStr.charAt(0);
    }
    
	
	public Class<Character> type() {
		return Character.class;
	}
	
	
	public Character valueFrom(String valueString) throws IllegalArgumentException {
		return charFrom(valueString);
	}
	
    
    protected String defaultAsString() {
        return Character.toString(defaultValue());
    }
}
