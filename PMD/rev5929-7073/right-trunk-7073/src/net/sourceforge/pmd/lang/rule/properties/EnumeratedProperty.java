
package net.sourceforge.pmd.lang.rule.properties;

import java.util.Enumeration;
import java.util.Map;

import net.sourceforge.pmd.PropertyDescriptorFactory;
import net.sourceforge.pmd.lang.rule.properties.factories.BasicPropertyDescriptorFactory;


public class EnumeratedProperty<E> extends AbstractEnumeratedProperty<E, Object> {
	
	public static final PropertyDescriptorFactory factory = new BasicPropertyDescriptorFactory<EnumeratedProperty>(Enumeration.class) {

		public EnumeratedProperty createWith(Map<String, String> valuesById) {

			return new EnumeratedProperty(
					nameIn(valuesById),
					descriptionIn(valuesById),
					labelsIn(valuesById),
					choicesIn(valuesById),
					indexIn(valuesById),
					0f
					);
		}
	};
	
	
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
