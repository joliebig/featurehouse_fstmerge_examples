
package net.sourceforge.squirrel_sql.plugins.h2.exp;


import net.sourceforge.squirrel_sql.client.session.mainpanel.objecttree.AbstractINodeExpanderTest;

import org.junit.Before;

public class SequenceParentExpanderTest extends AbstractINodeExpanderTest
{

	@Before
	public void setUp() throws Exception
	{ 
		classUnderTest = new SequenceParentExpander();
	}

}
