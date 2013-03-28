
package net.sourceforge.pmd.lang.rule.properties;

import java.util.Map;

import net.sourceforge.pmd.PropertyDescriptorFactory;
import net.sourceforge.pmd.lang.rule.properties.factories.BasicPropertyDescriptorFactory;


public class LongMultiProperty extends AbstractMultiNumericProperty<Long[]> {

	public static final PropertyDescriptorFactory factory = new BasicPropertyDescriptorFactory<LongMultiProperty>(Long[].class, numberFieldTypesByKey) {

		public LongMultiProperty createWith(Map<String, String> valuesById) {
			final String[] minMax = minMaxFrom(valuesById);
			Long[] defaultValues = longsIn(defaultValueIn(valuesById));
			return new LongMultiProperty(
					nameIn(valuesById),
					descriptionIn(valuesById),
					Long.parseLong(minMax[0]),
					Long.parseLong(minMax[1]),
					defaultValues,
					0f
					);
		};
	};
	
	
	public LongMultiProperty(String theName, String theDescription, Long min, Long max, Long[] theDefaults, float theUIOrder) {
		super(theName, theDescription, min, max, theDefaults, theUIOrder);
	}
	
	
	public Class<Long[]> type() {
		return Long[].class;
	}
	
	
	protected Object createFrom(String value) {
		return Long.valueOf(value);
	}

	
	protected Object[] arrayFor(int size) {
		return new Long[size];
	}
}
