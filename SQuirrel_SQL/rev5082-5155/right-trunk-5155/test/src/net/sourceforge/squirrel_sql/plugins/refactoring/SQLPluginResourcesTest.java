
package net.sourceforge.squirrel_sql.plugins.refactoring;


import static net.sourceforge.squirrel_sql.plugins.refactoring.RefactoringPlugin.BUNDLE_BASE_NAME;
import net.sourceforge.squirrel_sql.fw.util.AbstractResourcesTest;

import org.junit.Before;

public class SQLPluginResourcesTest extends AbstractResourcesTest
{

	@Before
	public void setUp() throws Exception
	{
		classUnderTest = new SQLPluginResources(BUNDLE_BASE_NAME, getMockPlugin());
	}

}
