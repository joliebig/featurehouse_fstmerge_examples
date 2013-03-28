package net.sourceforge.squirrel_sql.plugins.graph.xmlbeans;

 

import static org.junit.Assert.assertNull;
import net.sourceforge.squirrel_sql.BaseSQuirreLJUnit4TestCase;

import org.junit.Test;


public class ConstraintViewXmlBeanTest extends BaseSQuirreLJUnit4TestCase {

	ConstraintViewXmlBean classUnderTest = new ConstraintViewXmlBean();

	@Test
	public void testGetConstraintGraphXmlBean() throws Exception
	{
		classUnderTest.setConstraintGraphXmlBean(null);
		assertNull("aTestString", classUnderTest.getConstraintGraphXmlBean());
	}

	@Test
	public void testGetConstraintDataXmlBean() throws Exception
	{
		classUnderTest.setConstraintDataXmlBean(null);
		assertNull("aTestString", classUnderTest.getConstraintDataXmlBean());
	}

}
