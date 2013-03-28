
package net.sourceforge.squirrel_sql.plugins.laf;


import net.sourceforge.squirrel_sql.client.AppTestUtil;
import net.sourceforge.squirrel_sql.fw.util.AbstractResourcesTest;

import org.junit.Before;

public class LAFPluginResourcesTest extends AbstractResourcesTest
{

	@Before
	public void setUp() throws Exception
	{
		classUnderTest = new LAFPluginResources(AppTestUtil.getMockPlugin(mockHelper));
	}


}
