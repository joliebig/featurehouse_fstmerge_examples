package net.sourceforge.squirrel_sql.plugins.graph.xmlbeans;

 

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import net.sourceforge.squirrel_sql.BaseSQuirreLJUnit4TestCase;

import org.junit.Test;


public class TableFrameControllerXmlBeanTest extends BaseSQuirreLJUnit4TestCase {

	TableFrameControllerXmlBean classUnderTest = new TableFrameControllerXmlBean();

	@Test
	public void testGetSchema() throws Exception
	{
		classUnderTest.setSchema("aTestString");
		assertEquals("aTestString", classUnderTest.getSchema());
	}

	@Test
	public void testGetCatalog() throws Exception
	{
		classUnderTest.setCatalog("aTestString");
		assertEquals("aTestString", classUnderTest.getCatalog());
	}

	@Test
	public void testGetTablename() throws Exception
	{
		classUnderTest.setTablename("aTestString");
		assertEquals("aTestString", classUnderTest.getTablename());
	}

	@Test
	public void testGetTableFrameXmlBean() throws Exception
	{
		classUnderTest.setTableFrameXmlBean(null);
		assertEquals(null, classUnderTest.getTableFrameXmlBean());
	}

	@Test
	public void testGetColumnIfoXmlBeans() throws Exception
	{
		classUnderTest.setColumnIfoXmlBeans(null);
		assertNull(classUnderTest.getColumnIfoXmlBeans());
	}

	@Test
	public void testGetTablesExportedTo() throws Exception
	{
		classUnderTest.setTablesExportedTo(null);
		assertNull(classUnderTest.getTablesExportedTo());
	}

	@Test
	public void testGetConstraintViewXmlBeans() throws Exception
	{
		classUnderTest.setConstraintViewXmlBeans(null);
		assertNull(classUnderTest.getConstraintViewXmlBeans());
	}

	@Test
	public void testGetColumOrder() throws Exception
	{
		classUnderTest.setColumOrder(10);
		assertEquals(10, classUnderTest.getColumOrder());
	}

}
