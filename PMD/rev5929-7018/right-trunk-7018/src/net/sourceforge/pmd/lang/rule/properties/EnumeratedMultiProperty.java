
package net.sourceforge.pmd.lang.rule.properties;

import net.sourceforge.pmd.util.StringUtil;


public class EnumeratedMultiProperty<E> extends AbstractEnumeratedProperty<E, Object[]> {
	
	
	public EnumeratedMultiProperty(String theName, String theDescription, String[] theLabels, E[] theChoices, int[] choiceIndices, float theUIOrder) {
		super(theName, theDescription, theLabels, theChoices, choiceIndices, theUIOrder, true);
	}
	
	
	public Class<Object[]> type() {
		return Object[].class;
	}
	
	
	@Override
	public boolean isMultiValue() {
		return true;
	}
	
	
	@Override
	public String errorFor(Object value) {
		Object[] values = (Object[])value;
		for (int i=0; i<values.length; i++) {
			if (!labelsByChoice.containsKey(values[i])) {
				return nonLegalValueMsgFor(values[i]);
			}
		}
		return null;
	}
	
	
	public Object[] valueFrom(String value) throws IllegalArgumentException {
		String[] strValues = StringUtil.substringsOf(value, multiValueDelimiter());
		
		Object[] values = new Object[strValues.length];
		for (int i=0;i<values.length; i++) {
		    values[i] = choiceFrom(strValues[i]);
		}
		return values;
	}
	
	
	@Override
	public String asDelimitedString(Object[] value) {
		Object[] choices = value;
		
		StringBuilder sb = new StringBuilder();

		sb.append(labelsByChoice.get(choices[0]));
		
		for (int i=1; i<choices.length; i++) {
			sb.append(multiValueDelimiter());
			sb.append(labelsByChoice.get(choices[i]));
		}

		return sb.toString();
	}
}
