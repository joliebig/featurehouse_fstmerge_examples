
package net.sourceforge.squirrel_sql.client.session.mainpanel.objecttree.tabs.database;


import net.sourceforge.squirrel_sql.client.session.mainpanel.objecttree.tabs.AbstractBaseDataSetTabTest;

import org.junit.Before;

public class KeywordsTabTest extends AbstractBaseDataSetTabTest
{

	@Before
	public void setUp() throws Exception
	{
		super.setUp();
		classUnderTest = new KeywordsTab();
		clazz = KeywordsTab.class;
	}

}
