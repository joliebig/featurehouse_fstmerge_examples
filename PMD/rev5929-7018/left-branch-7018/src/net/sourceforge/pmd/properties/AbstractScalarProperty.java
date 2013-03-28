package net.sourceforge.pmd.properties;

import net.sourceforge.pmd.util.StringUtil;


public abstract class AbstractScalarProperty extends AbstractPMDProperty {

	
	public AbstractScalarProperty(String theName, String theDescription, Object theDefault, float theUIOrder) {
		super(theName, theDescription, theDefault, theUIOrder);
	}

	
	protected abstract Object createFrom(String value);
	
	
	protected abstract Object[] arrayFor(int size);
	
	
	public Object valueFrom(String valueString) throws IllegalArgumentException {
		
		if (maxValueCount() == 1) return createFrom(valueString);
		
		String[] strValues = StringUtil.substringsOf(valueString, multiValueDelimiter);
		
		Object[] values = arrayFor(strValues.length);
		for (int i=0; i<strValues.length; i++) values[i] = createFrom(strValues[i]);
		return values;
	}
}
