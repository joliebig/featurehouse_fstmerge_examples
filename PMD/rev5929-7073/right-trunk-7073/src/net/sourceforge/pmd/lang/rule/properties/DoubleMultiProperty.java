
package net.sourceforge.pmd.lang.rule.properties;

import java.util.Map;

import net.sourceforge.pmd.PropertyDescriptorFactory;
import net.sourceforge.pmd.lang.rule.properties.factories.BasicPropertyDescriptorFactory;



public class DoubleMultiProperty extends AbstractMultiNumericProperty<Double[]> {
	
	public static final PropertyDescriptorFactory factory = new BasicPropertyDescriptorFactory<DoubleMultiProperty>(Double[].class, numberFieldTypesByKey) {

		public DoubleMultiProperty createWith(Map<String, String> valuesById) {
			final String[] minMax = minMaxFrom(valuesById);
			Double[] defaultValues = doublesIn(defaultValueIn(valuesById));
			return new DoubleMultiProperty(
					nameIn(valuesById),
					descriptionIn(valuesById),
					Double.parseDouble(minMax[0]),
					Double.parseDouble(minMax[1]),
					defaultValues,
					0f
					);
		};
	};
	
	
	public DoubleMultiProperty(String theName, String theDescription, Double min, Double max, Double[] defaultValues, float theUIOrder) {
		super(theName, theDescription, min, max, defaultValues, theUIOrder);
	}
	
	
	public Class<Double[]> type() {
		return Double[].class;
	}

	
	protected Object createFrom(String value) {
		return Double.valueOf(value);
	}

	
	protected Object[] arrayFor(int size) {
		return new Double[size];
	}
}
