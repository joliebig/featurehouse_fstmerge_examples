
package net.sourceforge.pmd.lang.rule.properties;

import java.util.Map;

import net.sourceforge.pmd.PropertyDescriptorFactory;
import net.sourceforge.pmd.lang.rule.properties.factories.BasicPropertyDescriptorFactory;


public class BooleanProperty extends AbstractScalarProperty<Boolean> {

	
	public static final PropertyDescriptorFactory factory = new BasicPropertyDescriptorFactory<BooleanProperty>(Boolean.class) {

		public BooleanProperty createWith(Map<String, String> valuesById) {
			return new BooleanProperty(
					nameIn(valuesById),
					descriptionIn(valuesById),
					Boolean.valueOf(defaultValueIn(valuesById)),
					0f);
		}
	};
	
	public BooleanProperty(String theName, String theDescription, Boolean defaultValue, float theUIOrder) {
		super(theName, theDescription, Boolean.valueOf(defaultValue), theUIOrder);
	}
	
    
    public BooleanProperty(String theName, String theDescription, String defaultBoolStr, float theUIOrder) {
        this(theName, theDescription, boolFrom(defaultBoolStr), theUIOrder);
    }

    
    private static Boolean boolFrom(String boolStr) {
        return Boolean.valueOf(boolStr);
    }
    
	
	public Class<Boolean> type() {
		return Boolean.class;
	}

    
    protected String defaultAsString() {
        return Boolean.toString(defaultValue());
    }
	
	
	protected Object createFrom(String value) {
		return boolFrom(value);
	}
}
