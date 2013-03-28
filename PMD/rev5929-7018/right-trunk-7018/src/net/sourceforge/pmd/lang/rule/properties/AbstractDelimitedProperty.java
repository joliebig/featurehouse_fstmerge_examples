package net.sourceforge.pmd.lang.rule.properties;

import java.util.Map;


public abstract class AbstractDelimitedProperty<T> extends AbstractProperty<T> {

    private char multiValueDelimiter;
    
    private static final String DELIM_ID = "delimiter";
    
    
    protected AbstractDelimitedProperty(String theName, String theDescription, T theDefault, char delimiter, float theUIOrder) {
        super(theName, theDescription, theDefault, theUIOrder);
        
        multiValueDelimiter = delimiter;
    }

    protected static char delimiterIn(Map<String, String> parameters) {
        if (!parameters.containsKey(DELIM_ID)) {
            throw new IllegalArgumentException("missing delimiter value");
        }
        
        return parameters.get(DELIM_ID).charAt(0);
    }
    
    
    protected void addAttributesTo(Map<String, String> attributes) {
        super.addAttributesTo(attributes);
        
        attributes.put(DELIM_ID, Character.toString(multiValueDelimiter));
    }
    
    
    protected String defaultAsString() {
        return asDelimitedString(defaultValue(), multiValueDelimiter);
    }
    
    
    protected void multiValueDelimiter(char aDelimiter) {
        multiValueDelimiter = aDelimiter;
    }
    
    
    public char multiValueDelimiter() {
        return multiValueDelimiter;
    }
    
    
    @Override
    public boolean isMultiValue() {
        return true;
    }
}
