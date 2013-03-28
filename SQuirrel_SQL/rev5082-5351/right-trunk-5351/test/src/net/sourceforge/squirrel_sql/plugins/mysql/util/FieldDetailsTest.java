package net.sourceforge.squirrel_sql.plugins.mysql.util;

 

import static org.junit.Assert.assertEquals;
import net.sourceforge.squirrel_sql.BaseSQuirreLJUnit4TestCase;

import org.junit.Test;


public class FieldDetailsTest extends BaseSQuirreLJUnit4TestCase {

	FieldDetails classUnderTest = new FieldDetails();

	@Test
	public void testGetDefault() throws Exception
	{
		classUnderTest.setDefault("aTestString");
		assertEquals("aTestString", classUnderTest.getDefault());
	}

	@Test
	public void testGetFieldName() throws Exception
	{
		classUnderTest.setFieldName("aTestString");
		assertEquals("aTestString", classUnderTest.getFieldName());
	}

	@Test
	public void testGetFieldType() throws Exception
	{
		classUnderTest.setFieldType("aTestString");
		assertEquals("aTestString", classUnderTest.getFieldType());
	}

	@Test
	public void testGetFieldLength() throws Exception
	{
		classUnderTest.setFieldLength("aTestString");
		assertEquals("aTestString", classUnderTest.getFieldLength());
	}

	@Test
	public void testIsUnique() throws Exception
	{
		classUnderTest.setUnique(true);
		assertEquals(true, classUnderTest.IsUnique());
	}

	@Test
	public void testIsIndex() throws Exception
	{
		classUnderTest.setIndex(true);
		assertEquals(true, classUnderTest.IsIndex());
	}

	@Test
	public void testIsPrimary() throws Exception
	{
		classUnderTest.setPrimary(true);
		assertEquals(true, classUnderTest.IsPrimary());
	}

	@Test
	public void testIsBinary() throws Exception
	{
		classUnderTest.setBinary(true);
		assertEquals(true, classUnderTest.IsBinary());
	}

	@Test
	public void testIsNotNull() throws Exception
	{
		classUnderTest.setNotNull(true);
		assertEquals(true, classUnderTest.IsNotNull());
	}

	@Test
	public void testIsUnisigned() throws Exception
	{
		classUnderTest.setUnisigned(true);
		assertEquals(true, classUnderTest.IsUnisigned());
	}

	@Test
	public void testIsAutoIncrement() throws Exception
	{
		classUnderTest.setAutoIncrement(true);
		assertEquals(true, classUnderTest.IsAutoIncrement());
	}

	@Test
	public void testIsZeroFill() throws Exception
	{
		classUnderTest.setZeroFill(true);
		assertEquals(true, classUnderTest.IsZeroFill());
	}

}
