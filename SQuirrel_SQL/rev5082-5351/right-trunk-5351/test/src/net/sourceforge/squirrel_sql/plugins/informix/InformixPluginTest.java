
package net.sourceforge.squirrel_sql.plugins.informix;

import net.sourceforge.squirrel_sql.client.plugin.AbstractSessionPluginTest;

import org.junit.After;
import org.junit.Before;


public class InformixPluginTest extends AbstractSessionPluginTest
{	
	@Before
	public void setUp() throws Exception
	{
		classUnderTest = new InformixPlugin();
	}

	@After
	public void tearDown() throws Exception
	{
		classUnderTest = null;
	}

	@Override
	protected String getDatabaseProductName()
	{
		return "informix";
	}

	@Override
	protected String getDatabaseProductVersion()
	{
		return null;
	}		

}
