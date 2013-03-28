
package net.sourceforge.squirrel_sql.plugins.mysql.tab;


import net.sourceforge.squirrel_sql.client.session.mainpanel.objecttree.tabs.AbstractSourceTabTest;

import org.junit.Before;

public class MysqlProcedureSourceTabTest extends AbstractSourceTabTest
{

	@Before
	public void setUp() throws Exception
	{
		classUnderTest = new MysqlProcedureSourceTab(HINT);
	}

}
