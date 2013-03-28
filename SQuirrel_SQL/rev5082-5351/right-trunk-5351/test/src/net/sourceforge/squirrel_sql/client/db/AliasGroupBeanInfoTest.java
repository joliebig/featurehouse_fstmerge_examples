
package net.sourceforge.squirrel_sql.client.db;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class AliasGroupBeanInfoTest
{

	private AliasGroupBeanInfo classUnderTest = null;
	
	@Before
	public void setUp() throws Exception
	{
		classUnderTest = new AliasGroupBeanInfo();
	}

	@After
	public void tearDown() throws Exception
	{
		classUnderTest = null;
	}

	@Test
	public final void testGetPropertyDescriptors()
	{
		assertNotNull(classUnderTest.getPropertyDescriptors());
	}

}
