
package net.sourceforge.squirrel_sql.plugins.dbcopy;


import net.sourceforge.squirrel_sql.plugins.AbstractPluginTest;

import org.junit.After;
import org.junit.Before;

public class DBCopyPluginTest extends AbstractPluginTest
{
	@Before
	public void setUp() throws Exception
	{
		classUnderTest = new DBCopyPlugin();
	}

	@After
	public void tearDown() throws Exception
	{
		classUnderTest = null;
	}
}
