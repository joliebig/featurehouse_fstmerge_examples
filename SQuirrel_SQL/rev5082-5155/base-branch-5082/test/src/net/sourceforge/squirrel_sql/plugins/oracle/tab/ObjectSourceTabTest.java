
package net.sourceforge.squirrel_sql.plugins.oracle.tab;


import net.sourceforge.squirrel_sql.client.session.mainpanel.objecttree.tabs.AbstractSourceTabTest;

import org.junit.Before;

public class ObjectSourceTabTest extends AbstractSourceTabTest
{

	@Before
	public void setUp() throws Exception
	{
		classUnderTest = new ObjectSourceTab("data", "title", HINT);
	}

}
