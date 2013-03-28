
package net.sourceforge.squirrel_sql.plugins.hibernate;


import net.sourceforge.squirrel_sql.client.AppTestUtil;
import net.sourceforge.squirrel_sql.fw.util.AbstractResourcesTest;

import org.junit.Before;

public class HibernatePluginResourcesTest extends AbstractResourcesTest
{

	@Before
	public void setUp() throws Exception
	{
		classUnderTest = new HibernatePluginResources(AppTestUtil.getMockPlugin(mockHelper));
		super.getIconArgument = HibernatePluginResources.IKeys.CONNECTED_IMAGE;
	}

}
