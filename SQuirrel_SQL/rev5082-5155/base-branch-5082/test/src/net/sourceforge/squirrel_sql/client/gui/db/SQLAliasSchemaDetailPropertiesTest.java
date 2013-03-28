package net.sourceforge.squirrel_sql.client.gui.db;

 

import static org.junit.Assert.assertEquals;
import net.sourceforge.squirrel_sql.AbstractSerializableTest;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.gargoylesoftware.base.testing.EqualsTester;


public class SQLAliasSchemaDetailPropertiesTest extends AbstractSerializableTest {

	SQLAliasSchemaDetailProperties classUnderTest = new SQLAliasSchemaDetailProperties();

	@Before
	public void setUp() {
		super.serializableToTest = new SQLAliasSchemaDetailProperties();
	}
	
	@After
	public void tearDown() {
		super.serializableToTest = null;
	}
	
	@Test
	public void testGetSchemaName() throws Exception
	{
		classUnderTest.setSchemaName("aTestString");
		assertEquals("aTestString", classUnderTest.getSchemaName());
	}

	@Test
	public void testGetTable() throws Exception
	{
		classUnderTest.setTable(10);
		assertEquals(10, classUnderTest.getTable());
	}

	@Test
	public void testGetView() throws Exception
	{
		classUnderTest.setView(10);
		assertEquals(10, classUnderTest.getView());
	}

	@Test
	public void testGetProcedure() throws Exception
	{
		classUnderTest.setProcedure(10);
		assertEquals(10, classUnderTest.getProcedure());
	}

	@Test
	public void testHashcodeAndEquals() {
		SQLAliasSchemaDetailProperties a = new SQLAliasSchemaDetailProperties();
		a.setSchemaName("Schema1");
		SQLAliasSchemaDetailProperties b = new SQLAliasSchemaDetailProperties();
		b.setSchemaName("Schema1");
		SQLAliasSchemaDetailProperties c = new SQLAliasSchemaDetailProperties();
		c.setSchemaName("Schema2");
		SQLAliasSchemaDetailProperties d = new SQLAliasSchemaDetailProperties(){
			private static final long serialVersionUID = 1L;
		};
		d.setSchemaName("Schema1");
		
		new EqualsTester(a, b, c, d);
		
		a.setSchemaName(null);
		b.setSchemaName(null);
		d.setSchemaName(null);
		
		new EqualsTester(a, b, c, d);
	}
	
	@Test
	public void testCompareTo() {
		SQLAliasSchemaDetailProperties a = new SQLAliasSchemaDetailProperties();
		a.setSchemaName("Schema1");
		SQLAliasSchemaDetailProperties b = new SQLAliasSchemaDetailProperties();
		b.setSchemaName("Schema1");
		SQLAliasSchemaDetailProperties c = new SQLAliasSchemaDetailProperties();
		c.setSchemaName("Schema2");
		assertEquals("Schema1".compareTo("Schema1"), a.compareTo(b));
		assertEquals("Schema1".compareTo("Schema2"), a.compareTo(c));
	}
}