package net.sourceforge.squirrel_sql.plugins.graph.xmlbeans;

 

import static org.junit.Assert.assertNull;
import net.sourceforge.squirrel_sql.BaseSQuirreLJUnit4TestCase;

import org.junit.Test;


public class ConstraintGraphXmlBeanTest extends BaseSQuirreLJUnit4TestCase {

	ConstraintGraphXmlBean classUnderTest = new ConstraintGraphXmlBean();

	@Test
	public void testGetFoldingPointXmlBeans() throws Exception
	{
		classUnderTest.setFoldingPointXmlBeans(null);
		assertNull(classUnderTest.getFoldingPointXmlBeans());
	}

}
