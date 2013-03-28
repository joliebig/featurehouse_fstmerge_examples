
package net.sourceforge.squirrel_sql.plugins.SybaseASE;

import net.sourceforge.squirrel_sql.client.plugin.AbstractSessionPluginTest;

import org.junit.After;
import org.junit.Before;


public class SybaseASEPluginTest extends AbstractSessionPluginTest
{	
	@Before
	public void setUp() throws Exception
	{
		classUnderTest = new SybaseASEPlugin();
	}

	@After
	public void tearDown() throws Exception
	{
		classUnderTest = null;
	}

	@Override
	protected String getDatabaseProductName()
	{
		return "sybase";
	}

	@Override
	protected String getDatabaseProductVersion()
	{
		return null;
	}		

}
