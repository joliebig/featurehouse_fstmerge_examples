
package net.sourceforge.squirrel_sql.plugins.oracle;


import net.sourceforge.squirrel_sql.fw.util.AbstractResourcesTest;

import org.junit.Before;

public class OracleResourcesTest extends AbstractResourcesTest
{

	@Before
	public void setUp() throws Exception
	{
		classUnderTest = new OracleResources(OraclePlugin.BUNDLE_BASE_NAME, getMockPlugin());
	}

}
