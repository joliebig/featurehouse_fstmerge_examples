
package net.sourceforge.squirrel_sql.plugins.i18n;

import net.sourceforge.squirrel_sql.plugins.AbstractPluginTest;
import net.sourceforge.squirrel_sql.plugins.DatabaseProductVersionData;

import org.junit.After;
import org.junit.Before;


public class I18nPluginTest extends AbstractPluginTest implements DatabaseProductVersionData
{	
	@Before
	public void setUp() throws Exception
	{
		classUnderTest = new I18nPlugin();
	}

	@After
	public void tearDown() throws Exception
	{
		classUnderTest = null;
	}		

}
