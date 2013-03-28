
package net.sourceforge.squirrel_sql.client.session.mainpanel.objecttree.tabs.table;


import org.junit.Before;

public class ImportedKeysTabTest extends AbstractForeignKeysTabTest
{

	@Before
	public void setUp() throws Exception
	{
		super.setUp();
		classUnderTest = new ImportedKeysTab();
	}
	
}
