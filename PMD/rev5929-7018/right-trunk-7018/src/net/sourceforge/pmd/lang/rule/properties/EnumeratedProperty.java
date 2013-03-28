
package net.sourceforge.pmd.lang.rule.properties;


public class EnumeratedProperty<E> extends AbstractEnumeratedProperty<E, Object> {
	
	
	public EnumeratedProperty(String theName, String theDescription, String[] theLabels, E[] theChoices, int defaultIndex, float theUIOrder) {
		super(theName, theDescription, theLabels, theChoices, new int[] {defaultIndex}, theUIOrder, false);
	}
	
	
	public Class<Object> type() {
		return Object.class;
	}
	
	
	@Override
	public String errorFor(Object value) {
		return labelsByChoice.containsKey(value) ?
			null : nonLegalValueMsgFor(value);
	}
	
	
	public Object valueFrom(String value) throws IllegalArgumentException {
	    return choiceFrom(value);
	}
	
	
	@Override
	public String asDelimitedString(Object value) {
	    return labelsByChoice.get(value);
	}
}
