
package net.sourceforge.squirrel_sql.plugins.mssql;


import net.sourceforge.squirrel_sql.plugins.AbstractPluginTest;

import org.junit.After;
import org.junit.Before;

public class MssqlPluginTest extends AbstractPluginTest
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
}
