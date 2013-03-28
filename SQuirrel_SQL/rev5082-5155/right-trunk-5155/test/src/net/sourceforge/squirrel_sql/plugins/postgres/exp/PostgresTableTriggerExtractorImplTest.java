
package net.sourceforge.squirrel_sql.plugins.postgres.exp;


import net.sourceforge.squirrel_sql.client.session.mainpanel.objecttree.expanders.AbstractTableTriggerExtractorTest;

import org.junit.Before;

public class PostgresTableTriggerExtractorImplTest extends AbstractTableTriggerExtractorTest
{

	@Before
	public void setUp() throws Exception
	{
		classUnderTest = new PostgresTableTriggerExtractorImpl();
	}

}
