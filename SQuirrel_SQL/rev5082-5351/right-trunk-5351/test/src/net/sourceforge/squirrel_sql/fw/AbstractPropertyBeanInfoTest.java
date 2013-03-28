
package net.sourceforge.squirrel_sql.fw;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;

import java.beans.BeanInfo;
import java.beans.PropertyDescriptor;

import net.sourceforge.squirrel_sql.BaseSQuirreLJUnit4TestCase;

import org.junit.After;
import org.junit.Test;


public abstract class AbstractPropertyBeanInfoTest extends BaseSQuirreLJUnit4TestCase
{
	
	protected BeanInfo classUnderTest = null;

	@Test
	public void testGetPropertyDescriptors()
	{
		PropertyDescriptor[] result1 = classUnderTest.getPropertyDescriptors();
		assertNotNull(result1);
		
		PropertyDescriptor[] result2 = classUnderTest.getPropertyDescriptors();
		assertNotNull(result2);
		
		
		assertFalse("Expected two calls to getPropertyDescriptors() to produce two distinct objects, but " +
				"both references point to the same object.", result1 == result2);
	}

	@After
	public void tearDown() throws Exception
	{
		classUnderTest = null;
	}

}