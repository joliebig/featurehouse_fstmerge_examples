package net.sourceforge.squirrel_sql.plugins.mysql.prefs;

 

import net.sourceforge.squirrel_sql.BaseSQuirreLJUnit4TestCase;

import static org.junit.Assert.assertEquals;

import org.junit.Test;


public class MysqlPreferenceBeanTest extends BaseSQuirreLJUnit4TestCase {

	MysqlPreferenceBean classUnderTest = new MysqlPreferenceBean();

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
	public void testGetStatementSeparator() throws Exception
	{
		classUnderTest.setStatementSeparator("aTestString");
		assertEquals("aTestString", classUnderTest.getStatementSeparator());
	}

	@Test
	public void testGetProcedureSeparator() throws Exception
	{
		classUnderTest.setProcedureSeparator("aTestString");
		assertEquals("aTestString", classUnderTest.getProcedureSeparator());
	}

	@Test
	public void testGetLineComment() throws Exception
	{
		classUnderTest.setLineComment("aTestString");
		assertEquals("aTestString", classUnderTest.getLineComment());
	}

	@Test
	public void testIsRemoveMultiLineComments() throws Exception
	{
		classUnderTest.setRemoveMultiLineComments(true);
		assertEquals(true, classUnderTest.isRemoveMultiLineComments());
	}

	@Test
	public void testIsInstallCustomQueryTokenizer() throws Exception
	{
		classUnderTest.setInstallCustomQueryTokenizer(true);
		assertEquals(true, classUnderTest.isInstallCustomQueryTokenizer());
	}

}
