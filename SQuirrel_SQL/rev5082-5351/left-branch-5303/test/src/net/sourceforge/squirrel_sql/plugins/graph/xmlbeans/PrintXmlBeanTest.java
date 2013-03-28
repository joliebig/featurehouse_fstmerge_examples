package net.sourceforge.squirrel_sql.plugins.graph.xmlbeans;

 

import static org.junit.Assert.assertEquals;
import net.sourceforge.squirrel_sql.BaseSQuirreLJUnit4TestCase;

import org.junit.Test;


public class PrintXmlBeanTest extends BaseSQuirreLJUnit4TestCase {

	PrintXmlBean classUnderTest = new PrintXmlBean();

	@Test
	public void testGetEdgesScale() throws Exception
	{
		classUnderTest.setEdgesScale(10);
		assertEquals(10, classUnderTest.getEdgesScale());
	}

	@Test
	public void testIsShowEdges() throws Exception
	{
		classUnderTest.setShowEdges(true);
		assertEquals(true, classUnderTest.isShowEdges());
	}

}
