
package net.sourceforge.squirrel_sql.plugins.sqlparam;


import static net.sourceforge.squirrel_sql.client.AppTestUtil.getMockPlugin;
import net.sourceforge.squirrel_sql.fw.util.AbstractResourcesTest;

import org.junit.Before;

public class ResourcesTest extends AbstractResourcesTest
{

	@Before
	public void setUp() throws Exception
	{
		classUnderTest = new Resources(getMockPlugin(mockHelper));
	}

}
