
package net.sourceforge.squirrel_sql.plugins.db2.tab;


import net.sourceforge.squirrel_sql.client.session.mainpanel.objecttree.tabs.AbstractSourceTabTest;

import org.junit.Before;

public class ViewSourceTabTest extends AbstractSourceTabTest
{

	@Before
	public void setUp() throws Exception
	{
		classUnderTest = new ViewSourceTab(HINT, STMT_SEP, true);
	}

}
