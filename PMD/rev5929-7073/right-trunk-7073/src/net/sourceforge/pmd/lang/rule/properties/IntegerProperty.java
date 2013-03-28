
package net.sourceforge.pmd.lang.rule.properties;

import java.util.Map;

import net.sourceforge.pmd.PropertyDescriptorFactory;
import net.sourceforge.pmd.lang.rule.properties.factories.BasicPropertyDescriptorFactory;


public class IntegerProperty extends AbstractNumericProperty<Integer> {

	public static final PropertyDescriptorFactory factory = new BasicPropertyDescriptorFactory<IntegerProperty>(Integer.class, numberFieldTypesByKey) {

		public IntegerProperty createWith(Map<String, String> valuesById) {
			final String minMax[] = minMaxFrom(valuesById);		
			return new IntegerProperty(
					nameIn(valuesById),
					descriptionIn(valuesById),
					Integer.valueOf(minMax[0]),
					Integer.valueOf(minMax[1]),
					Integer.valueOf(numericDefaultValueIn(valuesById)),
					0f);
		}
	};		
	
	
	public IntegerProperty(String theName, String theDescription, Integer min, Integer max, Integer theDefault, float theUIOrder) {
		super(theName, theDescription, min, max, theDefault, theUIOrder);		
	}
	
	
	public IntegerProperty(String theName, String theDescription, String minStr, String maxStr, String defaultStr, float theUIOrder) {
        this(theName, theDescription, intFrom(minStr), intFrom(maxStr), intFrom(defaultStr), theUIOrder);       
    }
	
	
	public static Integer intFrom(String numberString) {
	    return Integer.valueOf(numberString);
	}
	
	
	public Class<Integer> type() {
		return Integer.class;
	}

	
	protected Object createFrom(String value) {
		return intFrom(value);
	}
}
