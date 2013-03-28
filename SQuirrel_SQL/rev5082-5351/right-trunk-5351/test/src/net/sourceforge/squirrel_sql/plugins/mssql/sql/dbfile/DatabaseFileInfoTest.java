package net.sourceforge.squirrel_sql.plugins.mssql.sql.dbfile;

 

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import net.sourceforge.squirrel_sql.BaseSQuirreLJUnit4TestCase;

import org.junit.Test;


public class DatabaseFileInfoTest extends BaseSQuirreLJUnit4TestCase {

	DatabaseFileInfo classUnderTest = new DatabaseFileInfo();

	@Test
	public void testGetOwner() throws Exception
	{
		classUnderTest.setOwner("aTestString");
		assertEquals("aTestString", classUnderTest.getOwner());
	}

	@Test
	public void testGetDatabaseSize() throws Exception
	{
		classUnderTest.setDatabaseSize("aTestString");
		assertEquals("aTestString", classUnderTest.getDatabaseSize());
	}

	@Test
	public void testGetCompatibilityLevel() throws Exception
	{
		classUnderTest.setCompatibilityLevel((short)2);
		assertEquals(2, classUnderTest.getCompatibilityLevel());
	}

	@Test
	public void testGetCreatedDate() throws Exception
	{
		classUnderTest.setCreatedDate("aTestString");
		assertEquals("aTestString", classUnderTest.getCreatedDate());
	}

	@Test
	public void testGetOption() throws Exception
	{
		classUnderTest.setOption("aTestOption", "aTestValue");
		assertEquals("aTestValue", classUnderTest.getOption("aTestOption"));
	}

	@Test
	public void testGetDatabaseName() throws Exception
	{
		classUnderTest.setDatabaseName("aTestString");
		assertEquals("aTestString", classUnderTest.getDatabaseName());
	}

	@Test
	public void testGetLogFiles() throws Exception
	{
		assertNotNull(classUnderTest.getLogFiles());
		assertEquals(0, classUnderTest.getLogFiles().length);
	}

	@Test
	public void testGetDataFiles() throws Exception
	{
		assertNotNull(classUnderTest.getDataFiles());
		assertEquals(0, classUnderTest.getDataFiles().length);

	}


}
