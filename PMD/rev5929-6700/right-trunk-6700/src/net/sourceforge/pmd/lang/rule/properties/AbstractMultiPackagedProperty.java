package net.sourceforge.pmd.lang.rule.properties;


public abstract class AbstractMultiPackagedProperty<T> extends AbstractPackagedProperty<T> {

    protected static final char DELIMITER = '|';
    
    
    protected AbstractMultiPackagedProperty(String theName,  String theDescription, T theDefault, String[] theLegalPackageNames, float theUIOrder) {
        super(theName, theDescription, theDefault, theLegalPackageNames, theUIOrder);
    }

    
    @Override
    public boolean isMultiValue() {
        return true;
    }
    
    
    protected String defaultAsString() {
        return asDelimitedString(defaultValue());
    }
}
