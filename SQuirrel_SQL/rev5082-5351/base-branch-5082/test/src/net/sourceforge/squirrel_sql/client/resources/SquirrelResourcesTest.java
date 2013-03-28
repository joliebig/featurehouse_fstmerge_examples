
package net.sourceforge.squirrel_sql.client.resources;


import net.sourceforge.squirrel_sql.fw.util.AbstractResourcesTest;

import org.junit.Before;

public class SquirrelResourcesTest extends AbstractResourcesTest
{

	@Before
	public void setUp() throws Exception
	{
		classUnderTest = new SquirrelResources(SquirrelResources.BUNDLE_BASE_NAME);
	}


}
