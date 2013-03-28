package net.sourceforge.squirrel_sql.plugins.graph.xmlbeans;

 

import static org.junit.Assert.assertEquals;
import net.sourceforge.squirrel_sql.BaseSQuirreLJUnit4TestCase;

import org.junit.Test;


public class ZoomerXmlBeanTest extends BaseSQuirreLJUnit4TestCase {

	ZoomerXmlBean classUnderTest = new ZoomerXmlBean();

	@Test
	public void testGetZoom() throws Exception
	{
		classUnderTest.setZoom(10);
		assertEquals(10, classUnderTest.getZoom(), 0);
	}

	@Test
	public void testGetOldZoom() throws Exception
	{
		classUnderTest.setOldZoom(10);
		assertEquals(10, classUnderTest.getOldZoom(), 0);
	}

	@Test
	public void testIsEnabled() throws Exception
	{
		classUnderTest.setEnabled(true);
		assertEquals(true, classUnderTest.isEnabled());
	}

	@Test
	public void testIsHideScrollbars() throws Exception
	{
		classUnderTest.setHideScrollbars(true);
		assertEquals(true, classUnderTest.isHideScrollbars());
	}

}
