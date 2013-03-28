
package net.sourceforge.squirrel_sql.plugins.oracle.tab;


import net.sourceforge.squirrel_sql.client.session.mainpanel.objecttree.tabs.AbstractBasePreparedStatementTabTest;
import net.sourceforge.squirrel_sql.plugins.postgres.tab.TriggerDetailsTab;

import org.junit.Before;

public class TriggerDetailsTabTest extends AbstractBasePreparedStatementTabTest
{

	@Before
	public void setUp() throws Exception
	{
		classUnderTest = new TriggerDetailsTab();
	}

}
