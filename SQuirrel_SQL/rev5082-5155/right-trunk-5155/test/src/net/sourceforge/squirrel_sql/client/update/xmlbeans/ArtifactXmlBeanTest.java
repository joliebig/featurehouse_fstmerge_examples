package net.sourceforge.squirrel_sql.client.update.xmlbeans;

 

import static org.junit.Assert.assertEquals;
import net.sourceforge.squirrel_sql.AbstractSerializableTest;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;


public class ArtifactXmlBeanTest extends AbstractSerializableTest {

	ArtifactXmlBean classUnderTest = null;
	
	@Before
	public void setUp() throws Exception
	{
		classUnderTest = new ArtifactXmlBean();
		super.serializableToTest = new ArtifactXmlBean();
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
	public void testGetType() throws Exception
	{
		classUnderTest.setType("aTestString");
		assertEquals("aTestString", classUnderTest.getType());
	}

	@Test
	public void testGetSize() throws Exception
	{
		classUnderTest.setSize(10);
		assertEquals(10, classUnderTest.getSize());
	}

	@Test
	public void testIsInstalled() throws Exception
	{
		classUnderTest.setInstalled(true);
		assertEquals(true, classUnderTest.isInstalled());
	}

	@Test
	public void testGetVersion() throws Exception
	{
		classUnderTest.setVersion("aTestString");
		assertEquals("aTestString", classUnderTest.getVersion());
	}

	@Test
	public void testGetChecksum() throws Exception
	{
		classUnderTest.setChecksum(10);
		assertEquals(10, classUnderTest.getChecksum());
	}

}
