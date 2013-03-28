
package net.sourceforge.squirrel_sql.plugins.syntax;

import static net.sourceforge.squirrel_sql.client.AppTestUtil.getMockPlugin;
import net.sourceforge.squirrel_sql.fw.util.AbstractResourcesTest;

import org.junit.Before;

public class SyntaxPluginResourcesTest extends AbstractResourcesTest
{

	@Before
	public void setUp() throws Exception
	{
		classUnderTest = new SyntaxPluginResources(getMockPlugin(mockHelper));
	}

}
