
package net.sourceforge.squirrel_sql.plugins.editextras;


import net.sourceforge.squirrel_sql.client.AppTestUtil;
import net.sourceforge.squirrel_sql.fw.util.AbstractResourcesTest;

import org.junit.Before;

public class ResourcesTest extends AbstractResourcesTest
{

	@Before
	public void setUp() throws Exception
	{
		classUnderTest = new Resources(AppTestUtil.getMockPlugin(mockHelper));
	}


}
