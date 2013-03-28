
package net.sourceforge.pmd.lang.rule.properties;

import java.util.Map;

import net.sourceforge.pmd.PropertyDescriptorFactory;
import net.sourceforge.pmd.lang.rule.properties.factories.BasicPropertyDescriptorFactory;


public class StringProperty extends AbstractProperty<String> {
	
	public static final PropertyDescriptorFactory factory = new BasicPropertyDescriptorFactory<StringProperty>(String.class) {

		public StringProperty createWith(Map<String, String> valuesById) {
			return new StringProperty(
					nameIn(valuesById),
					descriptionIn(valuesById),
					defaultValueIn(valuesById),
					0f);
		}
	};
	
	
	public StringProperty(String theName, String theDescription, String theDefaultValue, float theUIOrder) {
		super(theName, theDescription, theDefaultValue, theUIOrder);
	}
	
    
    protected String defaultAsString() {
        return defaultValue();
    }
	
	
	public Class<String> type() {
		return String.class;
	}
	
	
	public String valueFrom(String valueString) {
		return valueString;
	}
}
