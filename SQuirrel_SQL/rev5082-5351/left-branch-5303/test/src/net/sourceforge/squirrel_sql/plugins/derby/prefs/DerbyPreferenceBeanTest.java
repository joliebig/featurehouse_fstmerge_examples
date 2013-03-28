package net.sourceforge.squirrel_sql.plugins.derby.prefs;



import net.sourceforge.squirrel_sql.BaseSQuirreLJUnit4TestCase;

import static org.junit.Assert.assertEquals;

import org.junit.Test;


public class DerbyPreferenceBeanTest extends BaseSQuirreLJUnit4TestCase
{

	DerbyPreferenceBean classUnderTest = new DerbyPreferenceBean();

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
	public void testIsReadClobsFully() throws Exception
	{
		classUnderTest.setReadClobsFully(true);
		assertEquals(true, classUnderTest.isReadClobsFully());
	}

	@Test
	public void testIsInstallCustomQueryTokenizer() throws Exception
	{
		classUnderTest.setInstallCustomQueryTokenizer(true);
		assertEquals(true, classUnderTest.isInstallCustomQueryTokenizer());
	}

	@Test
	public void testClone()
	{
		
		DerbyPreferenceBean bean1 = new DerbyPreferenceBean();
		bean1.setClientName("bean1");
		bean1.setClientVersion("bean1");
		bean1.setInstallCustomQueryTokenizer(true);
		bean1.setLineComment("bean1");
		bean1.setProcedureSeparator("bean1");
		bean1.setReadClobsFully(true);
		bean1.setRemoveMultiLineComments(true);
		bean1.setStatementSeparator("bean1");

		
		DerbyPreferenceBean bean2 = bean1.clone();
		bean2.setClientName("bean2");
		bean2.setClientVersion("bean2");
		bean2.setInstallCustomQueryTokenizer(false);
		bean2.setLineComment("bean2");
		bean2.setProcedureSeparator("bean2");
		bean2.setReadClobsFully(false);
		bean2.setRemoveMultiLineComments(false);
		bean2.setStatementSeparator("bean2");

		
		assertEquals("bean1", bean1.getClientName());
		assertEquals("bean1", bean1.getClientVersion());
		assertEquals(true, bean1.isInstallCustomQueryTokenizer());
		assertEquals("bean1", bean1.getLineComment());
		assertEquals("bean1", bean1.getProcedureSeparator());
		assertEquals(true, bean1.isReadClobsFully());
		assertEquals(true, bean1.isRemoveMultiLineComments());
		assertEquals("bean1", bean1.getStatementSeparator());
	}

}
