
package net.sourceforge.squirrel_sql.plugins.db2.exp;

import net.sourceforge.squirrel_sql.client.session.mainpanel.objecttree.expanders.AbstractTableTriggerExtractorTest;

import org.junit.Before;
import org.junit.Test;

public class DB2TableTriggerExtractorImplTest extends AbstractTableTriggerExtractorTest
{

	@Before
	public void setUp() throws Exception
	{
		classUnderTest = new DB2TableTriggerExtractorImpl(false);
	}

	@Test
	public void testOS400() {
		classUnderTest = new DB2TableTriggerExtractorImpl(true);
		super.testGetTableTriggerQuery();
	}
}
