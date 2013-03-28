
package net.sourceforge.pmd.lang.rule.properties;

import java.util.Map;

import net.sourceforge.pmd.PropertyDescriptorFactory;
import net.sourceforge.pmd.lang.rule.properties.factories.BasicPropertyDescriptorFactory;



public class DoubleProperty extends AbstractNumericProperty<Double> {

	public static final PropertyDescriptorFactory factory = new BasicPropertyDescriptorFactory<DoubleProperty>(Double.class, numberFieldTypesByKey) {

		public DoubleProperty createWith(Map<String, String> valuesById) {
			final String minMax[] = minMaxFrom(valuesById);
			return new DoubleProperty(
					nameIn(valuesById),
					descriptionIn(valuesById),
					Double.valueOf(minMax[0]),
					Double.valueOf(minMax[1]),
					Double.valueOf(numericDefaultValueIn(valuesById)),
					0f);
		}
	};
	
	
	public DoubleProperty(String theName, String theDescription, Double min, Double max, Double theDefault, float theUIOrder) {
		super(theName, theDescription, min, max, theDefault, theUIOrder);		
	}
	
	
    public DoubleProperty(String theName, String theDescription, String minStr, String maxStr, String defaultStr, float theUIOrder) {
        this(theName, theDescription, doubleFrom(minStr), doubleFrom(maxStr), doubleFrom(defaultStr), theUIOrder);       
    }
	
    
    
    public static Double doubleFrom(String numberString) {
        return Double.valueOf(numberString);
    }
    
	
	public Class<Double> type() {
		return Double.class;
	}

	
	protected Object createFrom(String value) {
		return doubleFrom(value);
	}
}
