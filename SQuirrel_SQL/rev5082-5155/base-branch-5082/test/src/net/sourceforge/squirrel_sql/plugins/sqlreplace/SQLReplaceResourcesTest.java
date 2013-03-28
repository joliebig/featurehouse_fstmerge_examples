
package net.sourceforge.squirrel_sql.plugins.sqlreplace;

import static net.sourceforge.squirrel_sql.plugins.sqlreplace.SQLReplacePlugin.RESOURCE_PATH;
import net.sourceforge.squirrel_sql.fw.util.AbstractResourcesTest;

import org.junit.Before;

public class SQLReplaceResourcesTest extends AbstractResourcesTest
{

	@Before
	public void setUp() throws Exception
	{
		classUnderTest = new SQLReplaceResources(RESOURCE_PATH, getMockPlugin());
	}

}
