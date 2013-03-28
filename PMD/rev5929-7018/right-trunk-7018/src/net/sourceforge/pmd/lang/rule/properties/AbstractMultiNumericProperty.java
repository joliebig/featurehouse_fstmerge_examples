package net.sourceforge.pmd.lang.rule.properties;


public abstract class AbstractMultiNumericProperty<T> extends AbstractNumericProperty<T> {

    
    protected AbstractMultiNumericProperty(String theName, String theDescription, Number lower, Number upper, T theDefault, float theUIOrder) {
        super(theName, theDescription, lower, upper, theDefault, theUIOrder);
    }

    
    @Override
    public boolean isMultiValue() {
        return true;
    }
    
    
    protected String defaultAsString() {
        return asDelimitedString(defaultValue());
    }
}
