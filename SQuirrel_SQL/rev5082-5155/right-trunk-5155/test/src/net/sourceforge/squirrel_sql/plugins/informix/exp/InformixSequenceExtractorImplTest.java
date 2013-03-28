
package net.sourceforge.squirrel_sql.plugins.informix.exp;



import net.sourceforge.squirrel_sql.client.session.mainpanel.objecttree.expanders.AbstractSequenceParentExtractorTest;

import org.junit.Before;

public class InformixSequenceExtractorImplTest extends AbstractSequenceParentExtractorTest
{	
	@Before
	public void setUp() throws Exception
	{
		classUnderTest = new InformixSequenceExtractorImpl();
	}

}
