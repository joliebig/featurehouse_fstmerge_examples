
package net.sourceforge.pmd.lang.rule.properties;

import net.sourceforge.pmd.util.StringUtil;


public class CharacterProperty extends AbstractProperty {

	
	public CharacterProperty(String theName, String theDescription, char theDefault, float theUIOrder) {
		super(theName, theDescription, Character.valueOf(theDefault), theUIOrder);
	}

	
	public CharacterProperty(String theName, String theDescription, char[] theDefaults, float theUIOrder, char delimiter) {
		this(theName, theDescription, asCharacters(theDefaults), theUIOrder, delimiter);
	}
	
	
	public CharacterProperty(String theName, String theDescription, String theDefaults, float theUIOrder, char delimiter) {
		this(theName, theDescription, theDefaults.toCharArray(), theUIOrder, delimiter);
	}	
	
	
	public CharacterProperty(String theName, String theDescription, Character[] theDefaults, float theUIOrder, char delimiter) {
		super(theName, theDescription, theDefaults, theUIOrder);
		
		multiValueDelimiter(delimiter);
		maxValueCount(Integer.MAX_VALUE);
	}
	
	
	private static final Character[] asCharacters(char[] chars) {
		Character[] characters = new Character[chars.length];
		for (int i=0; i<chars.length; i++) {
		    characters[i] = Character.valueOf(chars[i]);
		}
		return characters;
	}
	
	
	public Class<Character> type() {
		return Character.class;
	}
	
	
	public Object valueFrom(String valueString) throws IllegalArgumentException {
		
		if (maxValueCount() == 1) {
			if (valueString.length() > 1) {
			    throw new IllegalArgumentException(valueString);
			}
			return Character.valueOf(valueString.charAt(0));
		}
		
		String[] values = StringUtil.substringsOf(valueString, multiValueDelimiter);
		
		Character[] chars = new Character[values.length];
		for (int i=0; i<values.length; i++) {
		    chars[i] = Character.valueOf(values[i].charAt(0));
		}
		return chars;
	}
}
