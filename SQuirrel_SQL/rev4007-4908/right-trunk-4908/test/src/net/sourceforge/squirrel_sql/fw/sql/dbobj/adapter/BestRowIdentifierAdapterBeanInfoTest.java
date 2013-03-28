
package net.sourceforge.squirrel_sql.fw.sql.dbobj.adapter;

import static org.junit.Assert.*;

import net.sourceforge.squirrel_sql.BaseSQuirreLJUnit4TestCase;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class BestRowIdentifierAdapterBeanInfoTest extends BaseSQuirreLJUnit4TestCase
{

	private BestRowIdentifierAdapterBeanInfo classUnderTest = null; 
	
	@Before
	public void setUp() throws Exception
	{
		classUnderTest = new BestRowIdentifierAdapterBeanInfo(); 
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
