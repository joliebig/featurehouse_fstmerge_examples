
package net.sourceforge.squirrel_sql.plugins.h2.exp;


import net.sourceforge.squirrel_sql.client.session.mainpanel.objecttree.expanders.AbstractTableIndexExtractorTest;

import org.junit.Before;

public class H2TableIndexExtractorImplTest extends AbstractTableIndexExtractorTest
{

	@Before
	public void setUp() throws Exception
	{
		classUnderTest = new H2TableIndexExtractorImpl();
	}

}
