
package org.firebirdsql.squirrel.tab;


import net.sourceforge.squirrel_sql.client.session.mainpanel.objecttree.tabs.AbstractBasePreparedStatementTabTest;

import org.junit.Before;

public class DomainDetailsTabTest extends AbstractBasePreparedStatementTabTest
{

	@Before
	public void setUp() throws Exception
	{
		classUnderTest = new DomainDetailsTab();
		super.databaseProductName = "Firebird";
	}

}
