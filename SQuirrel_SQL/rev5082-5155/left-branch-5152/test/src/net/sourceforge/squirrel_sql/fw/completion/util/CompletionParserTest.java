
package net.sourceforge.squirrel_sql.fw.completion.util;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class CompletionParserTest
{

	CompletionParser classUnderTest = null;
	
	String token1 = "schema1";
	String token2 = "table11";
	String qualifiedTableName = token1 + "." + token2; 
	String testSqlPrefix = "select col1 as alias1, col2 as alias2 from ";
	String testSql = testSqlPrefix + qualifiedTableName;
	
	@Before
	public void setUp() throws Exception
	{
		classUnderTest = new CompletionParser(testSql);
	}

	@After
	public void tearDown() throws Exception
	{
	}

	@Test
	public void testCompletionParserString()
	{
		
	}

	@Test
	public void testCompletionParserStringBoolean()
	{
		
	}

	@Test
	public void testIsQualified()
	{
		assertTrue(classUnderTest.isQualified());
	}

	@Test
	public void testGetStringToParse()
	{
		assertEquals(qualifiedTableName, classUnderTest.getStringToParse());
	}

	@Test
	public void testGetToken()
	{
		assertEquals(token1, classUnderTest.getToken(0));
		assertEquals(token2, classUnderTest.getToken(1));
	}

	@Test
	public void testSize()
	{
		assertEquals(2, classUnderTest.size());
	}

	@Test
	public void testGetStringToParsePosition()
	{
		assertEquals(testSqlPrefix.length()-1, classUnderTest.getStringToParsePosition());
	}

	@Test
	public void testGetStringToReplace()
	{
		assertEquals(token2, classUnderTest.getStringToReplace());
	}

	@Test
	public void testGetReplacementStart()
	{
		
		assertEquals((testSqlPrefix + token1 + ".").length(), classUnderTest.getReplacementStart());
	}

	@Test
	public void testGetTextTillCarret()
	{
		assertEquals(testSql.length(), classUnderTest.getTextTillCarret().length());
		assertEquals(testSql, classUnderTest.getTextTillCarret());
	}

	@Test
	public void testGetLastToken()
	{
		assertEquals(classUnderTest.getLastToken(), token2);
	}

	@Test
	public void testGetAllButFirst()
	{
		assertEquals(classUnderTest.getAllButFirst(), token2);
	}

}
