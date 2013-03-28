
package net.sourceforge.squirrel_sql.plugins.sqlbookmark;

import net.sourceforge.squirrel_sql.fw.util.AbstractResourcesTest;

import org.junit.Before;

public class SQLBookmarkResourcesTest extends AbstractResourcesTest
{

	@Before
	public void setUp() throws Exception
	{
		classUnderTest = new SQLBookmarkResources(SQLBookmarkPlugin.RESOURCE_PATH, getMockPlugin());
	}

}
