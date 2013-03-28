
package net.sourceforge.squirrel_sql.plugins.graph;


import net.sourceforge.squirrel_sql.client.AppTestUtil;
import net.sourceforge.squirrel_sql.fw.util.AbstractResourcesTest;

import org.junit.Before;

public class GraphPluginResourcesTest extends AbstractResourcesTest
{

	@Before
	public void setUp() throws Exception
	{
		classUnderTest = new GraphPluginResources(AppTestUtil.getMockPlugin(mockHelper));
		super.getIconArgument = GraphPluginResources.IKeys.PRINT_IMAGE;
	}

}
