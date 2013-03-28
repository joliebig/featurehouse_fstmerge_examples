
package net.sourceforge.squirrel_sql.plugins.codecompletion;

import net.sourceforge.squirrel_sql.fw.util.AbstractResourcesTest;

import org.junit.Before;

public class ResourcesTest extends AbstractResourcesTest
{

	@Before
	public void setUp() throws Exception
	{
		classUnderTest = new Resources(getMockPlugin());
	}

}
