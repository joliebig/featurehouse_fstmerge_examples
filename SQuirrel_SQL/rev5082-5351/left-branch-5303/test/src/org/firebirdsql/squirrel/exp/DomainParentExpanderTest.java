
package org.firebirdsql.squirrel.exp;


import net.sourceforge.squirrel_sql.client.session.mainpanel.objecttree.AbstractINodeExpanderTest;

import org.firebirdsql.squirrel.FirebirdPlugin;
import org.junit.Before;

public class DomainParentExpanderTest extends AbstractINodeExpanderTest
{

	@Before
	public void setUp() throws Exception
	{
		FirebirdPlugin mockPlugin = mockHelper.createMock(FirebirdPlugin.class);
		classUnderTest = new DomainParentExpander(mockPlugin);
	}

}
