
package net.sourceforge.squirrel_sql.plugins.sessionscript;

import net.sourceforge.squirrel_sql.client.plugin.AbstractPluginTest;
import net.sourceforge.squirrel_sql.client.plugin.DatabaseProductVersionData;

import org.junit.After;
import org.junit.Before;


public class SessionScriptPluginTest extends AbstractPluginTest implements DatabaseProductVersionData
{	
	@Before
	public void setUp() throws Exception
	{
		classUnderTest = new SessionScriptPlugin();
	}

	@After
	public void tearDown() throws Exception
	{
		classUnderTest = null;
	}		

}
