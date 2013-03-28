package net.sourceforge.squirrel_sql.plugins.sqlscript.prefs;

 

import net.sourceforge.squirrel_sql.BaseSQuirreLJUnit4TestCase;

import static org.junit.Assert.assertEquals;

import org.junit.Test;


public class SQLScriptPreferenceBeanTest extends BaseSQuirreLJUnit4TestCase {

	SQLScriptPreferenceBean classUnderTest = new SQLScriptPreferenceBean();

	@Test
	public void testGetClientName() throws Exception
	{
		classUnderTest.setClientName("aTestString");
		assertEquals("aTestString", classUnderTest.getClientName());
	}

	@Test
	public void testGetClientVersion() throws Exception
	{
		classUnderTest.setClientVersion("aTestString");
		assertEquals("aTestString", classUnderTest.getClientVersion());
	}

	@Test
	public void testIsQualifyTableNames() throws Exception
	{
		classUnderTest.setQualifyTableNames(true);
		assertEquals(true, classUnderTest.isQualifyTableNames());
	}

	@Test
	public void testIsDeleteRefAction() throws Exception
	{
		classUnderTest.setDeleteRefAction(true);
		assertEquals(true, classUnderTest.isDeleteRefAction());
	}

	@Test
	public void testGetDeleteAction() throws Exception
	{
		classUnderTest.setDeleteAction(10);
		assertEquals(10, classUnderTest.getDeleteAction());
	}

	@Test
	public void testGetUpdateAction() throws Exception
	{
		classUnderTest.setUpdateAction(10);
		assertEquals(10, classUnderTest.getUpdateAction());
	}

	@Test
	public void testIsUpdateRefAction() throws Exception
	{
		classUnderTest.setUpdateRefAction(true);
		assertEquals(true, classUnderTest.isUpdateRefAction());
	}

}
