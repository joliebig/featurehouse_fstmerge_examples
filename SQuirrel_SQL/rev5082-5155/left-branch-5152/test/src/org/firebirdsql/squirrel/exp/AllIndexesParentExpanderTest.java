
package org.firebirdsql.squirrel.exp;


import net.sourceforge.squirrel_sql.client.session.mainpanel.objecttree.AbstractINodeExpanderTest;

import org.junit.Before;

public class AllIndexesParentExpanderTest extends AbstractINodeExpanderTest
{

	@Before
	public void setUp() throws Exception
	{
		classUnderTest = new AllIndexesParentExpander();
	}

}
