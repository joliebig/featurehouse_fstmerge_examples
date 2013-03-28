package net.sourceforge.squirrel_sql.plugins.firebirdmanager.pref;

 

import net.sourceforge.squirrel_sql.BaseSQuirreLJUnit4TestCase;

import static org.junit.Assert.assertEquals;

import org.junit.Test;


public class FirebirdManagerPreferenceBeanTest extends BaseSQuirreLJUnit4TestCase {

	FirebirdManagerPreferenceBean classUnderTest = new FirebirdManagerPreferenceBean();

	@Test
	public void testGetPort() throws Exception
	{
		classUnderTest.setPort("aTestString");
		assertEquals("aTestString", classUnderTest.getPort());
	}

	@Test
	public void testGetServer() throws Exception
	{
		classUnderTest.setServer("aTestString");
		assertEquals("aTestString", classUnderTest.getServer());
	}

	@Test
	public void testGetDatabaseFolder() throws Exception
	{
		classUnderTest.setDatabaseFolder("aTestString");
		assertEquals("aTestString", classUnderTest.getDatabaseFolder());
	}

	@Test
	public void testGetPropertiesFolder() throws Exception
	{
		classUnderTest.setPropertiesFolder("aTestString");
		assertEquals("aTestString", classUnderTest.getPropertiesFolder());
	}

	@Test
	public void testGetUser() throws Exception
	{
		classUnderTest.setUser("aTestString");
		assertEquals("aTestString", classUnderTest.getUser());
	}

}
