package test.net.sourceforge.pmd.properties;

import static org.junit.Assert.assertTrue;
import junit.framework.Assert;
import net.sourceforge.pmd.PropertyDescriptor;
import net.sourceforge.pmd.util.CollectionUtil;

import org.junit.Test;


public abstract class AbstractPropertyDescriptorTester {

	private static final int multiValueCount = 10;
	
	public static final String punctuationChars  = "!@#$%^&*()_-+=[]{}\\|;:'\",.<>/?`~";
	public static final String whitespaceChars   = " \t\n";
	public static final String digitChars 		 = "0123456789";
	public static final String alphaChars 		 = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmniopqrstuvwxyz";
	public static final String alphaNumericChars = digitChars + alphaChars;
	public static final String allChars			 = punctuationChars + whitespaceChars + alphaNumericChars;

	
	
	protected abstract Object createValue(int count);
	
	
	protected abstract Object createBadValue(int count);
	
	
	protected abstract PropertyDescriptor createProperty(boolean multiValue);
	
	
	protected abstract PropertyDescriptor createBadProperty(boolean multiValue);
	
	@Test
	public void testConstructors() {
		
		PropertyDescriptor desc = createProperty(false);
		assertTrue(desc != null);
		
		try {
			createBadProperty(false);
			
		} catch (Exception ex) {
			return;	
		}
		
		Assert.fail("uncaught constructor exception");
	}
	
    @Test
    public void testAsDelimitedString() {
		
		Object testValue = createValue(multiValueCount);
		PropertyDescriptor pmdProp = createProperty(true);
		
		String storeValue = pmdProp.asDelimitedString(testValue);
		
		Object returnedValue = pmdProp.valueFrom(storeValue);
		
		assertTrue(CollectionUtil.areEqual(returnedValue, testValue));
	}
	
    @Test
    public void testValueFrom() {
		
		Object testValue = createValue(1);
		PropertyDescriptor pmdProp = createProperty(false);
		
		String storeValue = pmdProp.asDelimitedString(testValue);
		
		Object returnedValue = pmdProp.valueFrom(storeValue);
		
		assertTrue(CollectionUtil.areEqual(returnedValue, testValue));
	}
	
	
    @Test
    public void testErrorFor() {
		
		Object testValue = createValue(1);
		PropertyDescriptor pmdProp = createProperty(false);		
		String errorMsg = pmdProp.errorFor(testValue);
		assertTrue(errorMsg, errorMsg == null);			
		
		testValue = createValue(multiValueCount);				
		pmdProp = createProperty(true);
		errorMsg = pmdProp.errorFor(testValue);
		assertTrue(errorMsg, errorMsg == null);
		
    }
    
    @Test
    public void testErrorForBad() {
    	
    	PropertyDescriptor pmdProp = createProperty(false);    	
		Object testValue = createBadValue(1);
		String errorMsg = pmdProp.errorFor(testValue);			
		if (errorMsg == null) {
			Assert.fail("uncaught bad value: " + testValue);
		}
				
		testValue = createBadValue(multiValueCount);			
		pmdProp = createProperty(true);
		errorMsg = pmdProp.errorFor(testValue);
		if (errorMsg == null) {
			Assert.fail("uncaught bad value in: " + testValue);
		}
	}
	
    @Test
    public void testType() {
		
		PropertyDescriptor pmdProp = createProperty(false);

		assertTrue(pmdProp.type() != null);
	}
	
    public static boolean randomBool() {
    	return ((Math.random() * 100) % 2) == 0;
    }
    
	
	public static int randomInt() {
		
		int randomVal = (int) (Math.random() * 100 + 1D);
		return randomVal + (int) (Math.random() * 100000D);
	}
	
	
	public static int randomInt(int min, int max) {
		if (max < min) max = min;
		int range = Math.abs(max - min);
		int x = (int) (range * Math.random());
		return x + min;
	}
	
	
	public static float randomFloat(float min, float max) {
		
		return (float)randomDouble(min, max);
	}
	
	
	public static double randomDouble(double min, double max) {
		if (max < min) max = min;
		double range = Math.abs(max - min);
		double x = range * Math.random();
		return x + min;
	}
	
	
	public static char randomChar(char[] characters) {
		return characters[randomInt(0, characters.length-1)];
	}
	
	
	public static Object randomChoice(Object[] items) {
		return items[randomInt(0, items.length-1)];
	}
	
	
	protected static final char[] filter(char[] chars, char removeChar) {
		int count = 0;
		for (int i=0; i<chars.length; i++) if (chars[i] == removeChar) count++;
		char[] results = new char[chars.length - count];
		
		int index = 0;
		for (int i=0; i<chars.length; i++) {
			if (chars[i] != removeChar) results[index++] = chars[i];		
		}
		return results;
	}
}
