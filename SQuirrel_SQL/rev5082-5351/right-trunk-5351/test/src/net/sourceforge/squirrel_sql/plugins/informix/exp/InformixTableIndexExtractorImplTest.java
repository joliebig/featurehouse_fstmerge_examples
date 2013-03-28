
package net.sourceforge.squirrel_sql.plugins.informix.exp;


import net.sourceforge.squirrel_sql.client.session.mainpanel.objecttree.expanders.AbstractTableIndexExtractorTest;

import org.junit.Before;

public class InformixTableIndexExtractorImplTest extends AbstractTableIndexExtractorTest
{

	@Before
	public void setUp() throws Exception
	{
		classUnderTest = new InformixTableIndexExtractorImpl();
	}

}
