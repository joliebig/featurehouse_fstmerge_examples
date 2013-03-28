
package net.sourceforge.squirrel_sql.plugins.exportconfig;

import net.sourceforge.squirrel_sql.client.plugin.AbstractPluginTest;
import net.sourceforge.squirrel_sql.client.plugin.DatabaseProductVersionData;

import org.junit.After;
import org.junit.Before;


public class ExportConfigPluginTest extends AbstractPluginTest implements DatabaseProductVersionData
{	
	@Before
	public void setUp() throws Exception
	{
		classUnderTest = new ExportConfigPlugin();
	}

	@After
	public void tearDown() throws Exception
	{
		classUnderTest = null;
	}		

}
