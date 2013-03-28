
package net.sourceforge.pmd.lang.rule.properties;

import java.util.Map;

import net.sourceforge.pmd.PropertyDescriptorFactory;
import net.sourceforge.pmd.lang.rule.properties.factories.BasicPropertyDescriptorFactory;


public class IntegerMultiProperty extends AbstractMultiNumericProperty<Integer[]> {
	
	public static final PropertyDescriptorFactory factory = new BasicPropertyDescriptorFactory<IntegerMultiProperty>(Integer[].class, numberFieldTypesByKey) {

		public IntegerMultiProperty createWith(Map<String, String> valuesById) {
			final String[] minMax = minMaxFrom(valuesById);
			Integer[] defaultValues = integersIn(defaultValueIn(valuesById));
			return new IntegerMultiProperty(
					nameIn(valuesById),
					descriptionIn(valuesById),
					Integer.parseInt(minMax[0]),
					Integer.parseInt(minMax[1]),
					defaultValues,
					0f
					);
		};
	};	
	
	
	public IntegerMultiProperty(String theName, String theDescription, Integer min, Integer max, Integer[] theDefaults, float theUIOrder) {
		super(theName, theDescription, min, max, theDefaults, theUIOrder);
	}
	
	
	public Class<Integer[]> type() {
		return Integer[].class;
	}
	
	
	protected Object createFrom(String value) {
		return Integer.valueOf(value);
	}

	
	protected Object[] arrayFor(int size) {
		return new Integer[size];
	}
}
