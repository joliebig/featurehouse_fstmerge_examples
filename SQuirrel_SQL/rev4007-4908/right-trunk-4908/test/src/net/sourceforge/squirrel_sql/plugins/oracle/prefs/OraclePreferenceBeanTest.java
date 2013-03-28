package net.sourceforge.squirrel_sql.plugins.oracle.prefs;

 

import net.sourceforge.squirrel_sql.BaseSQuirreLJUnit4TestCase;

import static org.junit.Assert.assertEquals;

import org.junit.Test;


public class OraclePreferenceBeanTest extends BaseSQuirreLJUnit4TestCase {

	OraclePreferenceBean classUnderTest = new OraclePreferenceBean();

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

	@Test
	public void testIsExcludeRecycleBinTables() throws Exception
	{
		classUnderTest.setExcludeRecycleBinTables(true);
		assertEquals(true, classUnderTest.isExcludeRecycleBinTables());
	}

	@Test
	public void testIsShowErrorOffset() throws Exception
	{
		classUnderTest.setShowErrorOffset(true);
		assertEquals(true, classUnderTest.isShowErrorOffset());
	}

	@Test
	public void testGetSessionTimezone() throws Exception
	{
		classUnderTest.setSessionTimezone("aTestString");
		assertEquals("aTestString", classUnderTest.getSessionTimezone());
	}

	@Test
	public void testGetInitSessionTimezone() throws Exception
	{
		classUnderTest.setInitSessionTimezone(true);
		assertEquals(true, classUnderTest.getInitSessionTimezone());
	}

}
