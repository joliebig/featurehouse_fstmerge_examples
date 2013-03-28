
package net.sourceforge.squirrel_sql.plugins.mysql;

import net.sourceforge.squirrel_sql.fw.util.AbstractResourcesTest;

import org.junit.Before;

public class MysqlResourcesTest extends AbstractResourcesTest
{

	@Before
	public void setUp() throws Exception
	{
		classUnderTest = new MysqlResources(MysqlPlugin.class.getName(), getMockPlugin());
	}

}
