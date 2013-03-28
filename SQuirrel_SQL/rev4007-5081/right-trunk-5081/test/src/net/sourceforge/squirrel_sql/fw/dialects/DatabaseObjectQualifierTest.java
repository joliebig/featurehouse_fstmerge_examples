
package net.sourceforge.squirrel_sql.fw.dialects;

import static org.junit.Assert.*;

import net.sourceforge.squirrel_sql.BaseSQuirreLJUnit4TestCase;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class DatabaseObjectQualifierTest extends BaseSQuirreLJUnit4TestCase
{

	DatabaseObjectQualifier classUnderTest = null;
	
	String catalog = "aCatalog";
	String schema = "aSchema";

	
	@Before
	public void setUp() throws Exception
	{
	}

	@After
	public void tearDown() throws Exception
	{
		classUnderTest = null;
	}

	@Test
	public void testDatabaseObjectQualifier()
	{
		classUnderTest = new DatabaseObjectQualifier();
		Assert.assertNull(classUnderTest.getCatalog());
		Assert.assertNull(classUnderTest.getSchema());
	}

	@Test
	public void testDatabaseObjectQualifierStringString()
	{
		classUnderTest = new DatabaseObjectQualifier(catalog, schema);
		assertEquals(catalog, classUnderTest.getCatalog());
		assertEquals(schema, classUnderTest.getSchema());
	}

	@Test
	public void testSetGetCatalog()
	{
		classUnderTest = new DatabaseObjectQualifier();
		classUnderTest.setCatalog(catalog);
		assertEquals(catalog, classUnderTest.getCatalog());
	}


	@Test
	public void testSetGetSchema()
	{
		classUnderTest = new DatabaseObjectQualifier();
		classUnderTest.setSchema(catalog);
		assertEquals(catalog, classUnderTest.getSchema());
	}

}
