
package net.sourceforge.squirrel_sql.plugins.h2;

import net.sourceforge.squirrel_sql.client.plugin.AbstractSessionPluginTest;

import org.junit.After;
import org.junit.Before;


public class H2PluginTest extends AbstractSessionPluginTest
{	
	@Before
	public void setUp() throws Exception
	{
		classUnderTest = new H2Plugin();
	}

	@After
	public void tearDown() throws Exception
	{
		classUnderTest = null;
	}

	@Override
	protected String getDatabaseProductName()
	{
		return "H2";
	}

	@Override
	protected String getDatabaseProductVersion()
	{
		return null;
	}		

}
