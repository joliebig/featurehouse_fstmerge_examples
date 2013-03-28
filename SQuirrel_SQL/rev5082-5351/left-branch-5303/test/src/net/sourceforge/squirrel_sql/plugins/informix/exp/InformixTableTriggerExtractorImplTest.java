
package net.sourceforge.squirrel_sql.plugins.informix.exp;


import net.sourceforge.squirrel_sql.client.session.mainpanel.objecttree.expanders.AbstractTableTriggerExtractorTest;

import org.junit.Before;

public class InformixTableTriggerExtractorImplTest extends AbstractTableTriggerExtractorTest
{

	@Before
	public void setUp() throws Exception
	{
		classUnderTest = new InformixTableTriggerExtractorImpl();
	}

}
