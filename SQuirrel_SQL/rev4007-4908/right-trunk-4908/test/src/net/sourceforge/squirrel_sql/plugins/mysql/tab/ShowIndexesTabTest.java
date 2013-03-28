
package net.sourceforge.squirrel_sql.plugins.mysql.tab;


import net.sourceforge.squirrel_sql.client.session.mainpanel.objecttree.tabs.AbstractStatementTabTest;

import org.junit.Before;

public class ShowIndexesTabTest extends AbstractStatementTabTest
{

	@Before
	public void setUp() throws Exception
	{
		classUnderTest = new ShowIndexesTab();
	}

}
