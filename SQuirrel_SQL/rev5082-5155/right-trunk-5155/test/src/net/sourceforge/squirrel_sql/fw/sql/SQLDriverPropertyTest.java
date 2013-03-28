
package net.sourceforge.squirrel_sql.fw.sql;

import static org.junit.Assert.assertEquals;

import java.sql.DriverPropertyInfo;

import net.sourceforge.squirrel_sql.BaseSQuirreLJUnit4TestCase;

import org.junit.Before;
import org.junit.Test;

public class SQLDriverPropertyTest extends BaseSQuirreLJUnit4TestCase
{

	SQLDriverProperty classUnderTest = null;
	
	
	
	DriverPropertyInfo mockDriverPropertyInfo = new DriverPropertyInfo(PROP_NAME, PROP_VALUE);

	
	
	private static final String PROP_VALUE = "aPropValue";

	private static final String PROP_NAME = "aPropName";
	
	
	@Before
	public void setUp() throws Exception
	{
		classUnderTest = new SQLDriverProperty();
	}

	@Test (expected = IllegalArgumentException.class)
	public void testSetDriverPropertyInfo()
	{
		classUnderTest.setDriverPropertyInfo(null);
	}

	@Test
	public void testGetName() throws Exception
	{
		classUnderTest.setName("aTestString");
		assertEquals("aTestString", classUnderTest.getName());
	}

	@Test
	public void testGetValue() throws Exception
	{
		classUnderTest.setValue("aTestString");
		assertEquals("aTestString", classUnderTest.getValue());
	}

	@Test
	public void testIsSpecified() throws Exception
	{
		assertEquals(false, classUnderTest.isSpecified());
	}

	@Test
	public void testGetDriverPropertyInfo() throws Exception
	{
		mockHelper.replayAll();
		classUnderTest.setName(PROP_NAME);
		classUnderTest.setDriverPropertyInfo(mockDriverPropertyInfo);
		assertEquals(mockDriverPropertyInfo, classUnderTest.getDriverPropertyInfo());
		mockHelper.verifyAll();
	}
	
}
