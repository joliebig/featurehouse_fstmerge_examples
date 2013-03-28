package net.sourceforge.squirrel_sql.plugins.codecompletion.prefs;

 

import net.sourceforge.squirrel_sql.BaseSQuirreLJUnit4TestCase;

import static org.junit.Assert.assertEquals;

import org.junit.Test;


public class PrefixedConfigTest extends BaseSQuirreLJUnit4TestCase {

	PrefixedConfig classUnderTest = new PrefixedConfig();

	@Test
	public void testGetPrefix() throws Exception
	{
		classUnderTest.setPrefix("aTestString");
		assertEquals("aTestString", classUnderTest.getPrefix());
	}

	@Test
	public void testGetCompletionConfig() throws Exception
	{
		classUnderTest.setCompletionConfig(10);
		assertEquals(10, classUnderTest.getCompletionConfig());
	}

}
