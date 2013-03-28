
package net.sourceforge.squirrel_sql.plugins.derby;


import net.sourceforge.squirrel_sql.plugins.AbstractPluginTest;

import org.junit.After;
import org.junit.Before;

public class DerbyPluginTest extends AbstractPluginTest
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
}
