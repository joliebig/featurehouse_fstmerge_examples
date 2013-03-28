package net.sourceforge.squirrel_sql.plugins.graph.xmlbeans;

 

import net.sourceforge.squirrel_sql.BaseSQuirreLJUnit4TestCase;

import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertEquals;

import org.junit.Test;


public class GraphControllerXmlBeanTest extends BaseSQuirreLJUnit4TestCase {

	GraphControllerXmlBean classUnderTest = new GraphControllerXmlBean();

	@Test
	public void testGetTitle() throws Exception
	{
		classUnderTest.setTitle("aTestString");
		assertEquals("aTestString", classUnderTest.getTitle());
	}

	@Test
	public void testGetTableFrameControllerXmls() throws Exception
	{
		classUnderTest.setTableFrameControllerXmls(null);
		assertNull(classUnderTest.getTableFrameControllerXmls());
	}

	@Test
	public void testIsShowConstraintNames() throws Exception
	{
		classUnderTest.setShowConstraintNames(true);
		assertEquals(true, classUnderTest.isShowConstraintNames());
	}

	@Test
	public void testGetZoomerXmlBean() throws Exception
	{
		classUnderTest.setZoomerXmlBean(null);
		assertNull(classUnderTest.getZoomerXmlBean());
	}

	@Test
	public void testGetPrintXmlBean() throws Exception
	{
		classUnderTest.setPrintXmlBean(null);
		assertNull(classUnderTest.getPrintXmlBean());
	}

	@Test
	public void testIsShowQualifiedTableNames() throws Exception
	{
		classUnderTest.setShowQualifiedTableNames(true);
		assertEquals(true, classUnderTest.isShowQualifiedTableNames());
	}

}
