
package net.sourceforge.squirrel_sql.client.session.mainpanel.objecttree.tabs.table;


import org.junit.Before;

public class ExportedKeysTabTest extends AbstractForeignKeysTabTest
{

	@Before
	public void setUp() throws Exception
	{
		super.setUp();
		classUnderTest = new ExportedKeysTab();
	}
	
}
