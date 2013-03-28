
package net.sourceforge.squirrel_sql.plugins.sqlbookmarks;

import net.sourceforge.squirrel_sql.client.plugin.AbstractPluginTest;
import net.sourceforge.squirrel_sql.client.plugin.DatabaseProductVersionData;
import net.sourceforge.squirrel_sql.plugins.sqlbookmark.SQLBookmarkPlugin;

import org.junit.After;
import org.junit.Before;


public class SQLBookmarkPluginTest extends AbstractPluginTest implements DatabaseProductVersionData
{	
	@Before
	public void setUp() throws Exception
	{
		classUnderTest = new SQLBookmarkPlugin();
	}

	@After
	public void tearDown() throws Exception
	{
		classUnderTest = null;
	}		

}
