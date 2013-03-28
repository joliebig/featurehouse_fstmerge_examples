
package net.sourceforge.squirrel_sql.plugins.graph;


import net.sourceforge.squirrel_sql.plugins.AbstractPluginTest;

import org.junit.After;
import org.junit.Before;

public class GraphPluginTest extends AbstractPluginTest
{
	@Before
	public void setUp() throws Exception
	{
		classUnderTest = new GraphPlugin();
	}

	@After
	public void tearDown() throws Exception
	{
		classUnderTest = null;
	}
}
