package net.sourceforge.squirrel_sql.plugins.graph.xmlbeans;

 

import net.sourceforge.squirrel_sql.BaseSQuirreLJUnit4TestCase;

import static org.junit.Assert.assertEquals;

import org.junit.Test;


public class ColumnInfoXmlBeanTest extends BaseSQuirreLJUnit4TestCase {

	ColumnInfoXmlBean classUnderTest = new ColumnInfoXmlBean();

	@Test
	public void testGetIndex() throws Exception
	{
		classUnderTest.setIndex(10);
		assertEquals(10, classUnderTest.getIndex());
	}

	@Test
	public void testIsPrimaryKey() throws Exception
	{
		classUnderTest.setPrimaryKey(true);
		assertEquals(true, classUnderTest.isPrimaryKey());
	}

	@Test
	public void testGetColumnName() throws Exception
	{
		classUnderTest.setColumnName("aTestString");
		assertEquals("aTestString", classUnderTest.getColumnName());
	}

	@Test
	public void testGetColumnType() throws Exception
	{
		classUnderTest.setColumnType("aTestString");
		assertEquals("aTestString", classUnderTest.getColumnType());
	}

	@Test
	public void testGetColumnSize() throws Exception
	{
		classUnderTest.setColumnSize(10);
		assertEquals(10, classUnderTest.getColumnSize());
	}

	@Test
	public void testIsNullable() throws Exception
	{
		classUnderTest.setNullable(true);
		assertEquals(true, classUnderTest.isNullable());
	}

	@Test
	public void testGetImportedFromTable() throws Exception
	{
		classUnderTest.setImportedFromTable("aTestString");
		assertEquals("aTestString", classUnderTest.getImportedFromTable());
	}

	@Test
	public void testGetImportedColumn() throws Exception
	{
		classUnderTest.setImportedColumn("aTestString");
		assertEquals("aTestString", classUnderTest.getImportedColumn());
	}

	@Test
	public void testGetConstraintName() throws Exception
	{
		classUnderTest.setConstraintName("aTestString");
		assertEquals("aTestString", classUnderTest.getConstraintName());
	}

	@Test
	public void testGetDecimalDigits() throws Exception
	{
		classUnderTest.setDecimalDigits(10);
		assertEquals(10, classUnderTest.getDecimalDigits());
	}

}
