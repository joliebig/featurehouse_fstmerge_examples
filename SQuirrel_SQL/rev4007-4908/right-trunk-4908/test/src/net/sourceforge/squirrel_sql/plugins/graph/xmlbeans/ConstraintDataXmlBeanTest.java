package net.sourceforge.squirrel_sql.plugins.graph.xmlbeans;

 

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import net.sourceforge.squirrel_sql.BaseSQuirreLJUnit4TestCase;

import org.junit.Test;


public class ConstraintDataXmlBeanTest extends BaseSQuirreLJUnit4TestCase {

	ConstraintDataXmlBean classUnderTest = new ConstraintDataXmlBean();

	@Test
	public void testGetPkTableName() throws Exception
	{
		classUnderTest.setPkTableName("aTestString");
		assertEquals("aTestString", classUnderTest.getPkTableName());
	}

	@Test
	public void testGetFkTableName() throws Exception
	{
		classUnderTest.setFkTableName("aTestString");
		assertEquals("aTestString", classUnderTest.getFkTableName());
	}

	@Test
	public void testGetConstraintName() throws Exception
	{
		classUnderTest.setConstraintName("aTestString");
		assertEquals("aTestString", classUnderTest.getConstraintName());
	}

	@Test
	public void testGetColumnInfoXmlBeans() throws Exception
	{
		classUnderTest.setColumnInfoXmlBeans(null);
		assertNull(classUnderTest.getColumnInfoXmlBeans());
	}

}
