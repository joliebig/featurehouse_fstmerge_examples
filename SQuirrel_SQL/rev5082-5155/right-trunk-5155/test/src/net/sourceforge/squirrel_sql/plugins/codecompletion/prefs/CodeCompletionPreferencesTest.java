package net.sourceforge.squirrel_sql.plugins.codecompletion.prefs;

 

import static org.junit.Assert.assertEquals;
import net.sourceforge.squirrel_sql.BaseSQuirreLJUnit4TestCase;

import org.junit.Test;


public class CodeCompletionPreferencesTest extends BaseSQuirreLJUnit4TestCase {

	CodeCompletionPreferences classUnderTest = new CodeCompletionPreferences();

	@Test
	public void testGetGeneralCompletionConfig() throws Exception
	{
		classUnderTest.setGeneralCompletionConfig(10);
		assertEquals(10, classUnderTest.getGeneralCompletionConfig());
	}

	@SuppressWarnings("deprecation")
	@Test
	public void testGetPrefixedConfigs() throws Exception
	{
		classUnderTest.setPrefixedConfigs(null);
		assertEquals(null, classUnderTest.getPrefixedConfigs());
	}

	@Test
	public void testGetMaxLastSelectedCompletionNames() throws Exception
	{
		classUnderTest.setMaxLastSelectedCompletionNames(10);
		assertEquals(10, classUnderTest.getMaxLastSelectedCompletionNames());
	}

}
