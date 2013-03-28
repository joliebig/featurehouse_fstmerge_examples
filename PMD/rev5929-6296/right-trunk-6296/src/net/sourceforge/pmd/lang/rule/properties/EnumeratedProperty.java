
package net.sourceforge.pmd.lang.rule.properties;

import java.util.Map;

import net.sourceforge.pmd.util.CollectionUtil;
import net.sourceforge.pmd.util.StringUtil;


public class EnumeratedProperty<E> extends AbstractProperty {

	private Map<String, E>	choicesByLabel;
	private Map<E, String>	labelsByChoice;
	
	
	public EnumeratedProperty(String theName, String theDescription, String[] theLabels, E[] theChoices, float theUIOrder) {
		this(theName, theDescription, theLabels, theChoices, theUIOrder, 1);
	}
	
	
	public EnumeratedProperty(String theName, String theDescription, String[] theLabels, E[] theChoices, float theUIOrder, int maxValues) {
		super(theName, theDescription, theChoices[0], theUIOrder);

		choicesByLabel = CollectionUtil.mapFrom(theLabels, theChoices);
		labelsByChoice = CollectionUtil.invertedMapFrom(choicesByLabel);
		
		maxValueCount(maxValues);
	}
	
	
	public Class<Object> type() {
		return Object.class;
	}

	private String nonLegalValueMsgFor(Object value) {
		return value + " is not a legal value";
	}
	
	
	public String errorFor(Object value) {
		
		if (maxValueCount() == 1) {
			return labelsByChoice.containsKey(value) ?
				null : nonLegalValueMsgFor(value);
		}
		
		Object[] values = (Object[])value;
		for (int i=0; i<values.length; i++) {
			if (labelsByChoice.containsKey(values[i])) {
			    continue;
			}
			return nonLegalValueMsgFor(values[i]);
		}
		return null;
	}
	
	
	private E choiceFrom(String label) {
		E result = choicesByLabel.get(label);
		if (result != null) {
		    return result;
		}
		throw new IllegalArgumentException(label);
	}
	
	
	public Object valueFrom(String value) throws IllegalArgumentException {
		
		if (maxValueCount() == 1) {
		    return choiceFrom(value);
		}
		
		String[] strValues = StringUtil.substringsOf(value, multiValueDelimiter);
		
		Object[] values = new Object[strValues.length];
		for (int i=0;i<values.length; i++) {
		    values[i] = choiceFrom(strValues[i]);
		}
		return values;
	}
	
	
	public String asDelimitedString(Object value) {
		
		if (maxValueCount() == 1) {
		    return labelsByChoice.get(value);
		}
		
		Object[] choices = (Object[])value;
		
		StringBuffer sb = new StringBuffer();

		sb.append(labelsByChoice.get(choices[0]));
		
		for (int i=1; i<choices.length; i++) {
			sb.append(multiValueDelimiter);
			sb.append(labelsByChoice.get(choices[i]));
		}

		return sb.toString();
	}
}
