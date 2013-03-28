package test.net.sourceforge.pmd.properties;

import java.util.ArrayList;
import java.util.HashMap;

import net.sourceforge.pmd.PropertyDescriptor;
import net.sourceforge.pmd.lang.rule.properties.EnumeratedMultiProperty;
import net.sourceforge.pmd.lang.rule.properties.EnumeratedProperty;


public class EnumeratedPropertyTest extends AbstractPropertyDescriptorTester {

	private static final String[] keys = new String[] {
		"map",
		"emptyArray",
		"list",
		"string",
		};

    private static final Object[] values = new Object[] {
        new HashMap(),
        new Object[0],
        new ArrayList(),
        "Hello World!",
        };
    
	public EnumeratedPropertyTest() {
		super();
	}

	
	protected Object createValue(int count) {
		
		if (count == 1) return randomChoice(values);
		
		Object[] values = new Object[count];
		for (int i=0; i<values.length; i++) values[i] = createValue(1);
		return values;
	}

	
	protected Object createBadValue(int count) {
		
		if (count == 1) return Integer.toString(randomInt());		
		
		Object[] values = new Object[count];
		for (int i=0; i<values.length; i++) values[i] = createBadValue(1);
		return values;
	}
	
	
	protected PropertyDescriptor createProperty(boolean multiValue) {
		
		return multiValue ?
			new EnumeratedMultiProperty<Object>("testEnumerations", "Test enumerations with complex types", keys, values, new int[] {0,1}, 1.0f) :
			new EnumeratedProperty<Object>("testEnumerations", "Test enumerations with complex types", keys, values, 0, 1.0f);			
	}

	
	protected PropertyDescriptor createBadProperty(boolean multiValue) {
		
		return multiValue ?
			new EnumeratedMultiProperty<Object>("testEnumerations", "Test enumerations with complex types", keys, new Object[0], new int[] {99}, 1.0f) :
			new EnumeratedProperty<Object>("testEnumerations", "Test enumerations with complex types", new String[0], values, -1, 1.0f);
	}
	
    public static junit.framework.Test suite() {
        return new junit.framework.JUnit4TestAdapter(EnumeratedPropertyTest.class);
    }
}
