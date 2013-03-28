package net.sourceforge.squirrel_sql.plugins.graph.xmlbeans;

 

import net.sourceforge.squirrel_sql.BaseSQuirreLJUnit4TestCase;

import static org.junit.Assert.assertEquals;

import org.junit.Test;


public class FormatXmlBeanTest extends BaseSQuirreLJUnit4TestCase {

	FormatXmlBean classUnderTest = new FormatXmlBean();

	@Test
	public void testGetName() throws Exception
	{
		classUnderTest.setName("aTestString");
		assertEquals("aTestString", classUnderTest.getName());
	}

	@Test
	public void testGetWidth() throws Exception
	{
		classUnderTest.setWidth(10);
		assertEquals(10, classUnderTest.getWidth(), 0);
	}

	@Test
	public void testGetHeight() throws Exception
	{
		classUnderTest.setHeight(10);
		assertEquals(10, classUnderTest.getHeight(), 0);
	}

	@Test
	public void testIsSelected() throws Exception
	{
		classUnderTest.setSelected(true);
		assertEquals(true, classUnderTest.isSelected());
	}

	@Test
	public void testIsLandscape() throws Exception
	{
		classUnderTest.setLandscape(true);
		assertEquals(true, classUnderTest.isLandscape());
	}

}
