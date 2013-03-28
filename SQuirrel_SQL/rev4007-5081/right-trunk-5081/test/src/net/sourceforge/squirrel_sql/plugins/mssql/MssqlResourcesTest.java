
package net.sourceforge.squirrel_sql.plugins.mssql;


import net.sourceforge.squirrel_sql.fw.util.AbstractResourcesTest;

import org.junit.Before;

public class MssqlResourcesTest extends AbstractResourcesTest
{

	@Before
	public void setUp() throws Exception
	{
		classUnderTest = new MssqlResources(MssqlPlugin.class.getName(), getMockPlugin());
	}

}
