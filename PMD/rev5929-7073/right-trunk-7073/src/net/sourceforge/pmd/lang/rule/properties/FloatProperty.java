
package net.sourceforge.pmd.lang.rule.properties;

import java.util.Map;

import net.sourceforge.pmd.PropertyDescriptorFactory;
import net.sourceforge.pmd.lang.rule.properties.factories.BasicPropertyDescriptorFactory;


public class FloatProperty extends AbstractNumericProperty<Float> {

	public static final PropertyDescriptorFactory factory = new BasicPropertyDescriptorFactory<FloatProperty>(float.class, numberFieldTypesByKey) {

		public FloatProperty createWith(Map<String, String> valuesById) {
			final String minMax[] = minMaxFrom(valuesById);
			return new FloatProperty(
					nameIn(valuesById),
					descriptionIn(valuesById),
					Float.valueOf(minMax[0]),
					Float.valueOf(minMax[1]),
					Float.valueOf(numericDefaultValueIn(valuesById)),
					0f);
		}
	};
		
	
	
	public FloatProperty(String theName, String theDescription,	Float min, Float max, Float theDefault, float theUIOrder) {
		super(theName, theDescription, Float.valueOf(min), Float.valueOf(max), Float.valueOf(theDefault), theUIOrder);		
	}
	
	
    public FloatProperty(String theName, String theDescription, String minStr, String maxStr, String defaultStr, float theUIOrder) {
        this(theName, theDescription, floatFrom(minStr), floatFrom(maxStr), floatFrom(defaultStr), theUIOrder);      
    }
	
    
    public static Float floatFrom(String numberString) {
        return Float.valueOf(numberString);
    }
    
	
	public Class<Float> type() {
		return Float.class;
	}

	
	protected Object createFrom(String value) {
		return floatFrom(value);
	}
}
