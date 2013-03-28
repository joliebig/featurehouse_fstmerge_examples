
package net.sourceforge.squirrel_sql.plugins.firebirdmanager;

import net.sourceforge.squirrel_sql.plugins.AbstractPluginTest;
import net.sourceforge.squirrel_sql.plugins.DatabaseProductVersionData;

import org.junit.After;
import org.junit.Before;


public class FirebirdManagerPluginTest extends AbstractPluginTest implements DatabaseProductVersionData
{	
	@Before
	public void setUp() throws Exception
	{
		classUnderTest = new FirebirdManagerPlugin();
	}

	@After
	public void tearDown() throws Exception
	{
		classUnderTest = null;
	}		

}
