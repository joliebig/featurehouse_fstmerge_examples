
package net.sourceforge.pmd.lang.rule.properties;


public class BooleanMultiProperty extends AbstractScalarProperty<Boolean[]> {
	
	public BooleanMultiProperty(String theName, String theDescription, Boolean[] defaultValues, float theUIOrder) {
		super(theName, theDescription, defaultValues, theUIOrder);
	}
	
	
	public Class<Boolean[]> type() {
		return Boolean[].class;
	}

	
	@Override
	public boolean isMultiValue() {
		return true;
	}
	
	
	protected Object createFrom(String value) {
		return Boolean.valueOf(value);
	}

	
	protected Boolean[] arrayFor(int size) {
		return new Boolean[size];
	}
	
    
    protected String defaultAsString() {
        return asDelimitedString(defaultValue());
    }
}
