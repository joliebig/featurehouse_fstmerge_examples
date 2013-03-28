package net.sourceforge.squirrel_sql.client.update.xmlbeans;

 

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import net.sourceforge.squirrel_sql.AbstractSerializableTest;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;


public class ModuleXmlBeanTest extends AbstractSerializableTest {

	ModuleXmlBean classUnderTest = null;

	@Before
	public void setUp() throws Exception
	{
		classUnderTest = new ModuleXmlBean();
		super.serializableToTest = new ModuleXmlBean();
	}

	@After
	public void tearDown() throws Exception
	{
		classUnderTest = null;
		super.serializableToTest = null;
	}
	
	
	@Test
	public void testGetName() throws Exception
	{
		classUnderTest.setName("aTestString");
		assertEquals("aTestString", classUnderTest.getName());
	}

	@Test
	public void testGetArtifacts() throws Exception
	{
		classUnderTest.setArtifacts(null);
		assertNull(classUnderTest.getArtifacts());
	}

	@Test
	public void testaddArtifact() throws Exception
	{
	}

}
