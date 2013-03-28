
package net.sourceforge.squirrel_sql.plugins.laf;

import net.sourceforge.squirrel_sql.client.plugin.AbstractPluginTest;
import net.sourceforge.squirrel_sql.plugins.DatabaseProductVersionData;

import org.junit.After;
import org.junit.Before;


public class LAFPluginTest extends AbstractPluginTest implements DatabaseProductVersionData
{	
	@Before
	public void setUp() throws Exception
	{
		classUnderTest = new LAFPlugin();
	}

	@After
	public void tearDown() throws Exception
	{
		classUnderTest = null;
	}		

}
