
package net.sourceforge.squirrel_sql.plugins.postgres.tab;


import net.sourceforge.squirrel_sql.client.session.mainpanel.objecttree.tabs.AbstractSourceTabTest;

import org.junit.Before;

public class IndexSourceTabTest extends AbstractSourceTabTest {

	@Before
	public void setUp() throws Exception {
		classUnderTest = new IndexSourceTab(HINT, STMT_SEP);
	}

}
