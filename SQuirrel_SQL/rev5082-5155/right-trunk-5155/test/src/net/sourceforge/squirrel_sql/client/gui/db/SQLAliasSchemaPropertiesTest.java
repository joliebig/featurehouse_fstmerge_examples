package net.sourceforge.squirrel_sql.client.gui.db;



import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import net.sourceforge.squirrel_sql.AbstractSerializableTest;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;


public class SQLAliasSchemaPropertiesTest extends AbstractSerializableTest
{

	SQLAliasSchemaProperties classUnderTest = new SQLAliasSchemaProperties();

	@Before
	public void setUp() {
		super.serializableToTest = new SQLAliasSchemaProperties();
	}
	
	@After
	public void tearDown() {
		super.serializableToTest = null;
	}
	
	@Test
	public void testGetSchemaDetails() throws Exception
	{
		classUnderTest.setSchemaDetails(null);
		assertNull(classUnderTest.getSchemaDetails());
	}

	@Test
	public void testGetGlobalState() throws Exception
	{
		classUnderTest.setGlobalState(10);
		assertEquals(10, classUnderTest.getGlobalState());
	}

	@Test
	public void testIsCacheSchemaIndependentMetaData() throws Exception
	{
		classUnderTest.setCacheSchemaIndependentMetaData(true);
		assertEquals(true, classUnderTest.isCacheSchemaIndependentMetaData());
	}

	@Test
	public void testloadSchemaIndependentMetaData() throws Exception
	{
	}

	@Test
	public void testGetAllSchemaProceduresNotToBeCached() throws Exception
	{
		assertTrue(classUnderTest.getAllSchemaProceduresNotToBeCached().length == 0);
	}

	@Test
	public void testGetExpectsSomeCachedData() throws Exception
	{
		assertEquals(false, classUnderTest.getExpectsSomeCachedData());
	}

}
