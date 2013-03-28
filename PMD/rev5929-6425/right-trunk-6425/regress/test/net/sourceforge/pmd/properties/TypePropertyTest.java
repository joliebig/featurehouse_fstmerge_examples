package test.net.sourceforge.pmd.properties;

import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.Observer;
import java.util.Set;

import net.sourceforge.pmd.PropertyDescriptor;
import net.sourceforge.pmd.lang.rule.properties.TypeProperty;


public class TypePropertyTest extends AbstractPropertyDescriptorTester {

	private static final Class[] javaLangClasses = new Class[] { String.class, Integer.class, Thread.class, Object.class, Runtime.class };
	private static final Class[] javaUtilTypes = new Class[] { HashMap.class, Map.class, Comparator.class, Set.class, Observer.class };
	
	public TypePropertyTest() {
		super();
	}

	
	protected Object createValue(int count) {

		if (count == 1) return randomChoice(javaLangClasses);
		
		Object[] values = new Object[count];
		for (int i=0; i<values.length; i++) values[i] = createValue(1);
		return values;
	}

	
	protected Object createBadValue(int count) {

		if (count == 1) return randomChoice(javaUtilTypes);

		Object[] values = new Object[count];
		for (int i=0; i<values.length; i++) values[i] = createBadValue(1);
		return values;
	}

	
	protected PropertyDescriptor createProperty(boolean multiValue) {

		return multiValue ?
			new TypeProperty("testType", "Test type property", javaLangClasses, new String[] { "java.lang" }, 1.0f) :
			new TypeProperty("testType", "Test type property", javaLangClasses[0], new String[] { "java.lang" }, 1.0f);
			}

	
	protected PropertyDescriptor createBadProperty(boolean multiValue) {
		
		return multiValue ?
			new TypeProperty("testType", "Test type property", Set.class, new String[] { "java.lang" }, 1.0f) :
			new TypeProperty("testType", "Test type property", javaLangClasses, new String[] { "java.util" }, 1.0f);
			}

    public static junit.framework.Test suite() {
        return new junit.framework.JUnit4TestAdapter(TypePropertyTest.class);
    }
}
