package net.sourceforge.squirrel_sql.plugins.graph.xmlbeans;

 

import net.sourceforge.squirrel_sql.BaseSQuirreLJUnit4TestCase;

import static org.junit.Assert.assertEquals;

import org.junit.Test;


public class FoldingPointXmlBeanTest extends BaseSQuirreLJUnit4TestCase {

	FoldingPointXmlBean classUnderTest = new FoldingPointXmlBean();

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

}
