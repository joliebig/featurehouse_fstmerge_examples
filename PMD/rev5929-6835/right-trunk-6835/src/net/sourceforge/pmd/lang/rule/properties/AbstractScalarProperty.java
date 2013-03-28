
package net.sourceforge.pmd.lang.rule.properties;

import net.sourceforge.pmd.util.StringUtil;


public abstract class AbstractScalarProperty<T> extends AbstractProperty<T> {

	
	protected AbstractScalarProperty(String theName, String theDescription, T theDefault, float theUIOrder) {
		super(theName, theDescription, theDefault, theUIOrder);
	}

	
	protected abstract Object createFrom(String value);
	
	
	protected Object[] arrayFor(int size) {
	    if (isMultiValue()) {
		throw new IllegalStateException("Subclass '" + this.getClass().getSimpleName() + "' must implement the arrayFor(int) method.");
	    }
		throw new UnsupportedOperationException("Arrays not supported on single valued property descriptors.");
	}
	
	
	@SuppressWarnings("unchecked")
	public T valueFrom(String valueString) throws IllegalArgumentException {
		
		if (!isMultiValue()) {
		    return (T)createFrom(valueString);
		}
		
		String[] strValues = StringUtil.substringsOf(valueString, multiValueDelimiter());
		
		Object[] values = arrayFor(strValues.length);
		for (int i=0; i<strValues.length; i++) {
		    values[i] = createFrom(strValues[i]);
		}
		return (T)values;
	}
}
