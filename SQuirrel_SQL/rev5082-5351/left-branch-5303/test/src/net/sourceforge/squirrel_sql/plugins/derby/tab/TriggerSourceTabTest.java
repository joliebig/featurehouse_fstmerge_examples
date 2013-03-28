
package net.sourceforge.squirrel_sql.plugins.derby.tab;


import net.sourceforge.squirrel_sql.client.session.mainpanel.objecttree.tabs.AbstractSourceTabTest;

import org.junit.Before;

public class TriggerSourceTabTest extends AbstractSourceTabTest
{

	@Before
	public void setUp() throws Exception
	{
		classUnderTest = new TriggerSourceTab(HINT);
	}

}
