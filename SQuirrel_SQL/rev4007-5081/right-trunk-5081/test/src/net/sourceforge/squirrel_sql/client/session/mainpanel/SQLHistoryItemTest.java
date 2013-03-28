
package net.sourceforge.squirrel_sql.client.session.mainpanel;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import net.sourceforge.squirrel_sql.AbstractSerializableTest;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.gargoylesoftware.base.testing.EqualsTester;

public class SQLHistoryItemTest extends AbstractSerializableTest
{

	SQLHistoryItem classUnderTest = null;

	@Before
	public void setUp() throws Exception
	{
		classUnderTest = new SQLHistoryItem();
		super.serializableToTest = new SQLHistoryItem();
	}

	@After
	public void tearDown() throws Exception
	{
		classUnderTest = null;
		super.serializableToTest = null;
	}

	@Test
	public void testEqualsObject()
	{
		String sql1 = "select foo from foo";
		String sql2 = "select foo2 from foo2";
		String aliasName1 = "TestAlias";
		String aliasName2 = "TestAlias2";
		SQLHistoryItem item1 = new SQLHistoryItem(sql1, aliasName1);
		SQLHistoryItem item2 = new SQLHistoryItem(sql1, aliasName1);
		SQLHistoryItem item3 = new SQLHistoryItem(sql2, aliasName2);
		SQLHistoryItem item4 = new SQLHistoryItem(sql1, aliasName1)
		{
			private static final long serialVersionUID = 1L;
		};

		new EqualsTester(item1, item2, item3, item4);
	}

	@Test
	public void testGetSQL() throws Exception
	{
		classUnderTest.setSQL("aTestString");
		assertEquals("aTestString", classUnderTest.getSQL());
	}

	@Test
	public void testGetLastUsageTime() throws Exception
	{
		classUnderTest.setLastUsageTime(null);
		assertNull(classUnderTest.getLastUsageTime());
	}

	@Test
	public void testGetAliasName() throws Exception
	{
		classUnderTest.setAliasName("aTestString");
		assertEquals("aTestString", classUnderTest.getAliasName());
	}

	@Test (expected = IllegalArgumentException.class)
	public void testSqlNull() {
		classUnderTest.setSQL(null);
	}
	
	@Test (expected = IllegalArgumentException.class)
	public void testSqlConstructorNull() {
		classUnderTest = new SQLHistoryItem(null, "alias");
	}
	
	@Test
	public void testToString() {
		Assert.assertNotNull(classUnderTest.toString());
	}
		
}
