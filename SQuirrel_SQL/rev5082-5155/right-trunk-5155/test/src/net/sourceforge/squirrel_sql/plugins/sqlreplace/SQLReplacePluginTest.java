
package net.sourceforge.squirrel_sql.plugins.sqlreplace;

import net.sourceforge.squirrel_sql.client.plugin.AbstractPluginTest;
import net.sourceforge.squirrel_sql.plugins.DatabaseProductVersionData;

import org.junit.After;
import org.junit.Before;


public class SQLReplacePluginTest extends AbstractPluginTest implements DatabaseProductVersionData
{	
	@Before
	public void setUp() throws Exception
	{
		classUnderTest = new SQLReplacePlugin();
	}

	@After
	public void tearDown() throws Exception
	{
		classUnderTest = null;
	}		

}
