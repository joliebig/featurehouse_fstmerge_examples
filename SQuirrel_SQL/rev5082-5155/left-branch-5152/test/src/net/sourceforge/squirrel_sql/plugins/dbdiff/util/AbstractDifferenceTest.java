
package net.sourceforge.squirrel_sql.plugins.dbdiff.util;


import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import net.sourceforge.squirrel_sql.plugins.dbdiff.util.AbstractDifference.DiffType;

import org.junit.Test;

public class AbstractDifferenceTest
{

	AbstractDifference classUnderTest = new AbstractDifference();

	@Test
	public void testGetDifferenceType() throws Exception
	{
		classUnderTest.setDifferenceType(null);
		assertNull(classUnderTest.getDifferenceType());
		for (DiffType type :AbstractDifference.DiffType.values()) {
			classUnderTest.setDifferenceType(type);
			assertEquals(type, classUnderTest.getDifferenceType());
		}
	}

	@Test
	public void testGetTableName1() throws Exception
	{
		classUnderTest.setTableName1("aTestString");
		assertEquals("aTestString", classUnderTest.getTableName1());
	}

	@Test
	public void testGetSchema1() throws Exception
	{
		classUnderTest.setSchema1("aTestString");
		assertEquals("aTestString", classUnderTest.getSchema1());
	}

	@Test
	public void testGetCatalog1() throws Exception
	{
		classUnderTest.setCatalog1("aTestString");
		assertEquals("aTestString", classUnderTest.getCatalog1());
	}

	@Test
	public void testGetCatalog2() throws Exception
	{
		classUnderTest.setCatalog2("aTestString");
		assertEquals("aTestString", classUnderTest.getCatalog2());
	}

	@Test
	public void testGetSchema2() throws Exception
	{
		classUnderTest.setSchema2("aTestString");
		assertEquals("aTestString", classUnderTest.getSchema2());
	}

	@Test
	public void testGetTableName2() throws Exception
	{
		classUnderTest.setTableName2("aTestString");
		assertEquals("aTestString", classUnderTest.getTableName2());
	}

	@Test
	public void testGetDifferenceVal1() throws Exception
	{
		classUnderTest.setDifferenceVal1(null);
		assertNull(classUnderTest.getDifferenceVal1());
	}

	@Test
	public void testGetDifferenceVal2() throws Exception
	{
		classUnderTest.setDifferenceVal2(null);
		assertNull(classUnderTest.getDifferenceVal2());
	}

}
