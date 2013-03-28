
package net.sourceforge.squirrel_sql.plugins.dbcopy;

import static net.sourceforge.squirrel_sql.plugins.dbcopy.DBCopyPlugin.BUNDLE_BASE_NAME;
import net.sourceforge.squirrel_sql.fw.util.AbstractResourcesTest;

import org.junit.Before;

public class DBCopyPluginResourcesTest extends AbstractResourcesTest
{

	@Before
	public void setUp() throws Exception
	{
		classUnderTest = new DBCopyPluginResources(BUNDLE_BASE_NAME, getMockPlugin());
	}

}
