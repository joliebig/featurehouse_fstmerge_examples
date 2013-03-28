
package net.sourceforge.squirrel_sql.plugins.sqlbookmark;

import static net.sourceforge.squirrel_sql.client.AppTestUtil.getMockPlugin;
import net.sourceforge.squirrel_sql.fw.util.AbstractResourcesTest;

import org.junit.Before;

public class SQLBookmarkResourcesTest extends AbstractResourcesTest
{

	@Before
	public void setUp() throws Exception
	{
		classUnderTest = new SQLBookmarkResources(SQLBookmarkPlugin.RESOURCE_PATH, getMockPlugin(mockHelper));
	}

}
