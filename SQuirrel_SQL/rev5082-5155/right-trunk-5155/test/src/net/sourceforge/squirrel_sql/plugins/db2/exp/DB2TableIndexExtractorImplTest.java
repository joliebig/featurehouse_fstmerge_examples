
package net.sourceforge.squirrel_sql.plugins.db2.exp;





import net.sourceforge.squirrel_sql.client.session.mainpanel.objecttree.expanders.AbstractTableIndexExtractorTest;

import org.junit.Before;
import org.junit.Test;

public class DB2TableIndexExtractorImplTest extends AbstractTableIndexExtractorTest
{
	@Before
	public void setUp() throws Exception
	{
		classUnderTest = new DB2TableIndexExtractorImpl(false);
	}
	
	@Test
	public void testOS400() {
		classUnderTest = new DB2TableIndexExtractorImpl(true);
		super.testGetTableTriggerQuery();
	}

	
}
