
package net.sourceforge.squirrel_sql.plugins.derby;


import net.sourceforge.squirrel_sql.client.plugin.AbstractSessionPluginTest;

import org.junit.After;
import org.junit.Before;

public class DerbyPluginTest extends AbstractSessionPluginTest
{
	@Before
	public void setUp() throws Exception
	{
		classUnderTest = new DerbyPlugin();
	}

	@After
	public void tearDown() throws Exception
	{
		classUnderTest = null;
	}

	@Override
	protected String getDatabaseProductName()
	{
		return "Apache Derby";
	}

	@Override
	protected String getDatabaseProductVersion()
	{
		return null;
	}
}
