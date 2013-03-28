
package net.sourceforge.squirrel_sql.plugins.firebird;

import net.sourceforge.squirrel_sql.client.plugin.AbstractSessionPluginTest;

import org.firebirdsql.squirrel.FirebirdPlugin;
import org.junit.After;
import org.junit.Before;


public class FirebirdPluginTest extends AbstractSessionPluginTest
{	
	@Before
	public void setUp() throws Exception
	{
		classUnderTest = new FirebirdPlugin();
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
