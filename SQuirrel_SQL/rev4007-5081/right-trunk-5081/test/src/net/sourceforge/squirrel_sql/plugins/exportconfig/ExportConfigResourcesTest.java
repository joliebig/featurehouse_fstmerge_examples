
package net.sourceforge.squirrel_sql.plugins.exportconfig;


import net.sourceforge.squirrel_sql.fw.util.AbstractResourcesTest;

import org.junit.Before;

public class ExportConfigResourcesTest extends AbstractResourcesTest
{

	@Before
	public void setUp() throws Exception
	{
		classUnderTest = new ExportConfigResources(ExportConfigPlugin.class.getName(), getMockPlugin());
	}


}
