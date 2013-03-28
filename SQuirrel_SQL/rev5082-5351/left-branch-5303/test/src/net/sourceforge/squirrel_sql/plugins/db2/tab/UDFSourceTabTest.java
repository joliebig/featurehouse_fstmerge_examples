
package net.sourceforge.squirrel_sql.plugins.db2.tab;


import net.sourceforge.squirrel_sql.client.session.mainpanel.objecttree.tabs.AbstractSourceTabTest;

import org.junit.Before;

public class UDFSourceTabTest extends AbstractSourceTabTest
{

	@Before
	public void setUp() throws Exception
	{
		classUnderTest = new UDFSourceTab(HINT, STMT_SEP, true);
	}

}
