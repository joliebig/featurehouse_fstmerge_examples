
package net.sourceforge.pmd.lang.rule.properties;

import java.util.Map;

import net.sourceforge.pmd.PropertyDescriptorFactory;
import net.sourceforge.pmd.lang.rule.properties.factories.BasicPropertyDescriptorFactory;


public class LongProperty extends AbstractNumericProperty<Long> {

	public static final PropertyDescriptorFactory factory = new BasicPropertyDescriptorFactory<LongProperty>(Long.class, numberFieldTypesByKey) {

		public LongProperty createWith(Map<String, String> valuesById) {
			final String minMax[] = minMaxFrom(valuesById);		
			return new LongProperty(
					nameIn(valuesById),
					descriptionIn(valuesById),
					Long.valueOf(minMax[0]),
					Long.valueOf(minMax[1]),
					Long.valueOf(numericDefaultValueIn(valuesById)),
					0f);
		}
	};
	
	
	public LongProperty(String theName, String theDescription, Long min, Long max, Long theDefault, float theUIOrder) {
		super(theName, theDescription, min, max, theDefault, theUIOrder);		
	}
	
	
    public LongProperty(String theName, String theDescription, String minStr, String maxStr, String defaultStr, float theUIOrder) {
        this(theName, theDescription, longFrom(minStr), longFrom(maxStr), longFrom(defaultStr), theUIOrder);       
    }
	
    
    public static Long longFrom(String numberString) {
        return Long.valueOf(numberString);
    }
    
	
	public Class<Long> type() {
		return Long.class;
	}

	
	protected Object createFrom(String value) {
		return longFrom(value);
	}
}
