package net.sourceforge.squirrel_sql.plugins.refactoring.prefs;

 

import net.sourceforge.squirrel_sql.BaseSQuirreLJUnit4TestCase;

import static org.junit.Assert.assertEquals;

import org.junit.Test;


public class RefactoringPreferenceBeanTest extends BaseSQuirreLJUnit4TestCase {

	RefactoringPreferenceBean classUnderTest = new RefactoringPreferenceBean();

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
	public void testIsQuoteIdentifiers() throws Exception
	{
		classUnderTest.setQuoteIdentifiers(true);
		assertEquals(true, classUnderTest.isQuoteIdentifiers());
	}

}
