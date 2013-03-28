
package net.sourceforge.squirrel_sql.plugins.sessionscript;

import net.sourceforge.squirrel_sql.fw.util.AbstractResourcesTest;

import org.junit.Before;

public class SessionScriptResourcesTest extends AbstractResourcesTest
{

	@Before
	public void setUp() throws Exception
	{
		classUnderTest = new SessionScriptResources(SessionScriptPlugin.BUNDLE_BASE_NAME, getMockPlugin());
	}

}
