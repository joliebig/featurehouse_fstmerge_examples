package net.sourceforge.squirrel_sql.plugins.mssql.sql.dbfile;

 

import static org.junit.Assert.assertEquals;
import net.sourceforge.squirrel_sql.BaseSQuirreLJUnit4TestCase;

import org.junit.Test;


public class DatabaseFileTest extends BaseSQuirreLJUnit4TestCase {

	DatabaseFile classUnderTest = new DatabaseFile();

	@Test
	public void testGetName() throws Exception
	{
		classUnderTest.setName("aTestString");
		assertEquals("aTestString", classUnderTest.getName());
	}

	@Test
	public void testGetId() throws Exception
	{
		classUnderTest.setId((short)2);
		assertEquals(2, classUnderTest.getId());
	}

	@Test
	public void testGetSize() throws Exception
	{
		classUnderTest.setSize("aTestString");
		assertEquals("aTestString", classUnderTest.getSize());
	}

	@Test
	public void testGetFilename() throws Exception
	{
		classUnderTest.setFilename("aTestString");
		assertEquals("aTestString", classUnderTest.getFilename());
	}

	@Test
	public void testGetFilegroup() throws Exception
	{
		classUnderTest.setFilegroup("aTestString");
		assertEquals("aTestString", classUnderTest.getFilegroup());
	}

	@Test
	public void testGetMaxSize() throws Exception
	{
		classUnderTest.setMaxSize("aTestString");
		assertEquals("aTestString", classUnderTest.getMaxSize());
	}

	@Test
	public void testGetGrowth() throws Exception
	{
		classUnderTest.setGrowth("aTestString");
		assertEquals("aTestString", classUnderTest.getGrowth());
	}

	@Test
	public void testGetUsage() throws Exception
	{
		classUnderTest.setUsage("aTestString");
		assertEquals("aTestString", classUnderTest.getUsage());
	}

}
