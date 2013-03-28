package net.sourceforge.squirrel_sql.plugins.graph.xmlbeans;

 

import static org.junit.Assert.assertEquals;
import net.sourceforge.squirrel_sql.BaseSQuirreLJUnit4TestCase;

import org.junit.Test;


public class TableFrameXmlBeanTest extends BaseSQuirreLJUnit4TestCase {

	TableFrameXmlBean classUnderTest = new TableFrameXmlBean();

	@Test
	public void testGetX() throws Exception
	{
		classUnderTest.setX(10);
		assertEquals(10, classUnderTest.getX());
	}

	@Test
	public void testGetY() throws Exception
	{
		classUnderTest.setY(10);
		assertEquals(10, classUnderTest.getY());
	}

	@Test
	public void testGetWidht() throws Exception
	{
		classUnderTest.setWidht(10);
		assertEquals(10, classUnderTest.getWidht());
	}

	@Test
	public void testGetHeight() throws Exception
	{
		classUnderTest.setHeight(10);
		assertEquals(10, classUnderTest.getHeight());
	}

}
