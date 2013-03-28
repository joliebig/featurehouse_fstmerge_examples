
package net.sourceforge.squirrel_sql.plugins.mysql.expander;


import net.sourceforge.squirrel_sql.client.session.mainpanel.objecttree.expanders.AbstractTableTriggerExtractorTest;

import org.junit.Before;

public class MysqlTableTriggerExtractorImplTest extends AbstractTableTriggerExtractorTest
{

	@Before
	public void setUp() throws Exception
	{
		classUnderTest = new MysqlTableTriggerExtractorImpl();
	}

}
