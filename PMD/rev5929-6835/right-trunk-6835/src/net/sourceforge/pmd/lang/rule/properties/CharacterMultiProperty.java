
package net.sourceforge.pmd.lang.rule.properties;

import java.util.Map;

import net.sourceforge.pmd.util.StringUtil;


public class CharacterMultiProperty extends AbstractDelimitedProperty<Character[]> {
	
	public CharacterMultiProperty(String theName, String theDescription, Character[] theDefaults, float theUIOrder, char delimiter) {
		super(theName, theDescription, theDefaults, delimiter, theUIOrder);
		
		if (theDefaults != null) {
			for (int i=0; i<theDefaults.length; i++) {
				if (theDefaults[i].charValue() == delimiter) {
					throw new IllegalArgumentException("Cannot include the delimiter in the set of defaults");
				}
			}
		}
	}
	
	
	public CharacterMultiProperty(String theName, String theDescription, String theDefaults, Map<String, String> otherParams) {
	    this(theName, theDescription, charsIn(theDefaults, delimiterIn(otherParams)), 0.0f, delimiterIn(otherParams));
	}
	
	private static Character[] charsIn(String charString, char delimiter) {
	    
	    String[] values = StringUtil.substringsOf(charString, delimiter);
	    Character[] chars = new Character[values.length];
	    
	    for (int i=0; i<values.length;i++) {
	        if (values.length != 1) {
	            throw new IllegalArgumentException("missing/ambiguous character value");
	        }
	        chars[i] = values[i].charAt(0);
	    }
	    return chars;
	}
	
	
	public Class<Character[]> type() {
		return Character[].class;
	}
		
	
	public Character[] valueFrom(String valueString) throws IllegalArgumentException {
		String[] values = StringUtil.substringsOf(valueString, multiValueDelimiter());
		
		Character[] chars = new Character[values.length];
		for (int i=0; i<values.length; i++) {
		    chars[i] = Character.valueOf(values[i].charAt(0));
		}
		return chars;
	}
}
