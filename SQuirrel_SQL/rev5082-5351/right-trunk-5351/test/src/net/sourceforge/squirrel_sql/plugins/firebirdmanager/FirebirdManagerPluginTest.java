
package net.sourceforge.squirrel_sql.plugins.firebirdmanager;

import net.sourceforge.squirrel_sql.client.plugin.AbstractSessionPluginTest;
import net.sourceforge.squirrel_sql.client.plugin.DatabaseProductVersionData;

import org.junit.After;
import org.junit.Before;


public class FirebirdManagerPluginTest extends AbstractSessionPluginTest implements DatabaseProductVersionData
{	
	@Before
	public void setUp() throws Exception
	{
		classUnderTest = new FirebirdManagerPlugin();
	}

	@After
	public void tearDown() throws Exception
	{
		classUnderTest = null;
	}

	@Override
	protected String getDatabaseProductName()
	{
		return "Firebird";
	}

	@Override
	protected String getDatabaseProductVersion()
	{
		return null;
	}		

}
