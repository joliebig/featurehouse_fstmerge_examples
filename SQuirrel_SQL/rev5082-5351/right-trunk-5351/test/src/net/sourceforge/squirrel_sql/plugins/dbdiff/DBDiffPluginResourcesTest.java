
package net.sourceforge.squirrel_sql.plugins.dbdiff;


import net.sourceforge.squirrel_sql.client.AppTestUtil;
import net.sourceforge.squirrel_sql.fw.util.AbstractResourcesTest;

import org.junit.Before;

public class DBDiffPluginResourcesTest extends AbstractResourcesTest
{

	@Before
	public void setUp() throws Exception
	{
		classUnderTest = new DBDiffPluginResources(DBDiffPlugin.BUNDLE_BASE_NAME, AppTestUtil.getMockPlugin(mockHelper));
	}

}
