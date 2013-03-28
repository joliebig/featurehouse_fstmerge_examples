
package net.sourceforge.squirrel_sql.fw.id;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class UidIdentifierBeanInfoTest
{

	private UidIdentifierBeanInfo classUnderTest = null;
	
	@Before
	public void setUp() throws Exception
	{
		classUnderTest = new UidIdentifierBeanInfo();
	}

	@After
	public void tearDown() throws Exception
	{
		classUnderTest = null;
	}

	@Test
	public void testGetPropertyDescriptors()
	{
		assertNotNull(classUnderTest.getPropertyDescriptors());
	}

}
