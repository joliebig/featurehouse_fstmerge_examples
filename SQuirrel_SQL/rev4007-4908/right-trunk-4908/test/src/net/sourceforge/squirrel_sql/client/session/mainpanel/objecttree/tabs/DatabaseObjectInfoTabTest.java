
package net.sourceforge.squirrel_sql.client.session.mainpanel.objecttree.tabs;


import org.junit.Before;

public class DatabaseObjectInfoTabTest extends AbstractBaseDataSetTabTest
{

	@Before
	public void setUp() throws Exception
	{
		super.setUp();
		classUnderTest = new DatabaseObjectInfoTab();
		clazz = DatabaseObjectInfoTab.class;
	}

}
