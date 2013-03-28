
package net.sourceforge.pmd.lang.rule.properties;

import java.util.Map;

import net.sourceforge.pmd.PropertyDescriptorFactory;
import net.sourceforge.pmd.lang.rule.properties.factories.BasicPropertyDescriptorFactory;


public class FloatMultiProperty extends AbstractMultiNumericProperty<Float[]> {
	
	public static final PropertyDescriptorFactory factory = new BasicPropertyDescriptorFactory<FloatMultiProperty>(Float[].class, numberFieldTypesByKey) {

		public FloatMultiProperty createWith(Map<String, String> valuesById) {
			final String[] minMax = minMaxFrom(valuesById);
			Float[] defaultValues = floatsIn(defaultValueIn(valuesById));
			return new FloatMultiProperty(
					nameIn(valuesById),
					descriptionIn(valuesById),
					Float.parseFloat(minMax[0]),
					Float.parseFloat(minMax[1]),
					defaultValues,
					0f
					);
		};
	};
	
	
	public FloatMultiProperty(String theName, String theDescription, Float min, Float max, Float[] defaultValues, float theUIOrder) {
		super(theName, theDescription, min, max, defaultValues, theUIOrder);
	}
	
	
	public Class<Float[]> type() {
		return Float[].class;
	}
	
	
	protected Object createFrom(String value) {
		return Float.valueOf(value);
	}

	
	protected Object[] arrayFor(int size) {
		return new Float[size];
	}
}
