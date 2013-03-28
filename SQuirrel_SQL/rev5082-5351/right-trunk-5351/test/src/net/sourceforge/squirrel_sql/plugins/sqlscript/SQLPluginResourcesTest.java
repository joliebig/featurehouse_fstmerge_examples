
package net.sourceforge.squirrel_sql.plugins.sqlscript;

import static net.sourceforge.squirrel_sql.client.AppTestUtil.getMockPlugin;
import net.sourceforge.squirrel_sql.fw.util.AbstractResourcesTest;

import org.junit.Before;

public class SQLPluginResourcesTest extends AbstractResourcesTest
{

	@Before
	public void setUp() throws Exception
	{
		classUnderTest = new SQLPluginResources(SQLScriptPlugin.BUNDLE_BASE_NAME, getMockPlugin(mockHelper));
	}

}
