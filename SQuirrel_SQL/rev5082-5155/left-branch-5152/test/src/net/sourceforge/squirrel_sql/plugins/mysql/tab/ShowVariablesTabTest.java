
package net.sourceforge.squirrel_sql.plugins.mysql.tab;


import net.sourceforge.squirrel_sql.client.session.mainpanel.objecttree.tabs.AbstractStatementTabTest;

import org.junit.Before;

public class ShowVariablesTabTest extends AbstractStatementTabTest
{

	@Before
	public void setUp() throws Exception
	{
		classUnderTest = new ShowVariablesTab();
	}

}
