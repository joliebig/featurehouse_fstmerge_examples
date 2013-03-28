
package org.firebirdsql.squirrel;


import net.sourceforge.squirrel_sql.fw.util.AbstractResourcesTest;

import org.junit.Before;

public class FirebirdResourcesTest extends AbstractResourcesTest
{

	@Before
	public void setUp() throws Exception
	{
		classUnderTest = new FirebirdResources(FirebirdPlugin.class.getName(), getMockPlugin());
	}

}
