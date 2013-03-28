
package net.sourceforge.squirrel_sql.fw.gui;


import static org.junit.Assert.assertEquals;

import net.sourceforge.squirrel_sql.AbstractSerializableTest;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;


public class FontInfoTest extends AbstractSerializableTest
{

	FontInfo classUnderTest = null;
	
	@Before
	public void setUp() throws Exception
	{
		classUnderTest = new FontInfo();
		super.serializableToTest = new FontInfo();
	}

	@After
	public void tearDown() throws Exception
	{
		classUnderTest = null;
		super.serializableToTest = null;
	}

	@Test
	public void testGetSize() throws Exception
	{
		classUnderTest.setSize(10);
		assertEquals(10, classUnderTest.getSize());
	}

	@Test
	public void testGetFamily() throws Exception
	{
		classUnderTest.setFamily("aTestString");
		assertEquals("aTestString", classUnderTest.getFamily());
	}

	@Test
	public void testIsBold() throws Exception
	{
		assertEquals(false, classUnderTest.isBold());
	}

	@Test
	public void testIsItalic() throws Exception
	{
		assertEquals(false, classUnderTest.isItalic());
	}

}
