
package net.sourceforge.squirrel_sql.plugins.firebird;

import net.sourceforge.squirrel_sql.plugins.AbstractPluginTest;
import net.sourceforge.squirrel_sql.plugins.DatabaseProductVersionData;

import org.firebirdsql.squirrel.FirebirdPlugin;
import org.junit.After;
import org.junit.Before;


public class FirebirdPluginTest extends AbstractPluginTest implements DatabaseProductVersionData
{	
	@Before
	public void setUp() throws Exception
	{
		classUnderTest = new FirebirdPlugin();
	}

	@After
	public void tearDown() throws Exception
	{
		classUnderTest = null;
	}		

}
