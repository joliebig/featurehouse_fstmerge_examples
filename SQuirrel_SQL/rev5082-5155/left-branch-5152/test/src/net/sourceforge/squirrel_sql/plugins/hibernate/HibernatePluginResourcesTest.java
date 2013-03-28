
package net.sourceforge.squirrel_sql.plugins.hibernate;


import net.sourceforge.squirrel_sql.fw.util.AbstractResourcesTest;

import org.junit.Before;

public class HibernatePluginResourcesTest extends AbstractResourcesTest
{

	@Before
	public void setUp() throws Exception
	{
		classUnderTest = new HibernatePluginResources(getMockPlugin());
		super.getIconArgument = HibernatePluginResources.IKeys.CONNECTED_IMAGE;
	}

}
