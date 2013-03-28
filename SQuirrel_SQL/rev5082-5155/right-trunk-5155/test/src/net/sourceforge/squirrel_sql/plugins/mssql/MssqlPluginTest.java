
package net.sourceforge.squirrel_sql.plugins.mssql;


import net.sourceforge.squirrel_sql.client.plugin.AbstractSessionPluginTest;

import org.junit.After;
import org.junit.Before;

public class MssqlPluginTest extends AbstractSessionPluginTest
{
	@Before
	public void setUp() throws Exception
	{
		classUnderTest = new MssqlPlugin();
	}

	@After
	public void tearDown() throws Exception
	{
		classUnderTest = null;
	}

	@Override
	protected String getDatabaseProductName()
	{
		return "microsoft";
	}

	@Override
	protected String getDatabaseProductVersion()
	{
		return null;
	}
}
