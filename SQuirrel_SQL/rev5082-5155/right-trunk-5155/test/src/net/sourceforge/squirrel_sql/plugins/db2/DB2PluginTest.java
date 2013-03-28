
package net.sourceforge.squirrel_sql.plugins.db2;

import net.sourceforge.squirrel_sql.client.plugin.AbstractSessionPluginTest;
import net.sourceforge.squirrel_sql.plugins.DatabaseProductVersionData;

import org.junit.After;
import org.junit.Before;


public class DB2PluginTest extends AbstractSessionPluginTest implements DatabaseProductVersionData
{	
	@Before
	public void setUp() throws Exception
	{
		classUnderTest = new DB2Plugin();
	}

	@After
	public void tearDown() throws Exception
	{
		classUnderTest = null;
	}

	@Override
	protected String getDatabaseProductName()
	{
		return "DB2";
	}

	@Override
	protected String getDatabaseProductVersion()
	{
		return null;
	}		

}
