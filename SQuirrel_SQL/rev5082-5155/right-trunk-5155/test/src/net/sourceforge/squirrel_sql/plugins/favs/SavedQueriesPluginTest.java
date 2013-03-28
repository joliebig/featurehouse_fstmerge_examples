
package net.sourceforge.squirrel_sql.plugins.favs;

import net.sourceforge.squirrel_sql.client.plugin.AbstractPluginTest;
import net.sourceforge.squirrel_sql.plugins.DatabaseProductVersionData;

import org.junit.After;
import org.junit.Before;


public class SavedQueriesPluginTest extends AbstractPluginTest implements DatabaseProductVersionData
{	
	@Before
	public void setUp() throws Exception
	{
		classUnderTest = new SavedQueriesPlugin();
	}

	@After
	public void tearDown() throws Exception
	{
		classUnderTest = null;
	}		

}
