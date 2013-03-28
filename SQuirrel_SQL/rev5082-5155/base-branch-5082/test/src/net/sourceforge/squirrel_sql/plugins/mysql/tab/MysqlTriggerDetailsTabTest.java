
package net.sourceforge.squirrel_sql.plugins.mysql.tab;


import net.sourceforge.squirrel_sql.client.session.mainpanel.objecttree.tabs.AbstractBasePreparedStatementTabTest;

import org.junit.Before;

public class MysqlTriggerDetailsTabTest extends AbstractBasePreparedStatementTabTest
{

	@Before
	public void setUp() throws Exception
	{
		classUnderTest = new MysqlTriggerDetailsTab();
	}

}
