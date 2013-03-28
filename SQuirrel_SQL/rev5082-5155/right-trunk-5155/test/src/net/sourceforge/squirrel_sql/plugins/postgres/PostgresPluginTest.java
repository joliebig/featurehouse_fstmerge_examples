
package net.sourceforge.squirrel_sql.plugins.postgres;

import net.sourceforge.squirrel_sql.client.plugin.AbstractSessionPluginTest;

import org.junit.After;
import org.junit.Before;


public class PostgresPluginTest extends AbstractSessionPluginTest
{	
	@Before
	public void setUp() throws Exception
	{
		classUnderTest = new PostgresPlugin();
	}

	@After
	public void tearDown() throws Exception
	{
		classUnderTest = null;
	}

	@Override
	protected String getDatabaseProductName()
	{
		return POSTGRESQL_PRODUCT_NAME;
	}

	@Override
	protected String getDatabaseProductVersion()
	{
		return POSTGRESQL_8_2_PRODUCT_VERSION;
	}		

}
