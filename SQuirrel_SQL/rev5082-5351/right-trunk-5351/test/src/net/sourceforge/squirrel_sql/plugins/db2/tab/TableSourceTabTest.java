
package net.sourceforge.squirrel_sql.plugins.db2.tab;


import static org.easymock.EasyMock.expect;
import net.sourceforge.squirrel_sql.client.session.mainpanel.objecttree.tabs.AbstractSourceTabTest;
import net.sourceforge.squirrel_sql.fw.sql.DatabaseObjectType;
import net.sourceforge.squirrel_sql.fw.sql.ITableInfo;

import org.junit.Before;

public class TableSourceTabTest extends AbstractSourceTabTest
{

	@Before
	public void setUp() throws Exception
	{
		classUnderTest = new TableSourceTab(HINT, STMT_SEP, true);
		ITableInfo mockTableInfo = mockHelper.createMock(ITableInfo.class);
		super.mockDatabaseObjectInfo = mockTableInfo;
   	expect(mockDatabaseObjectInfo.getDatabaseObjectType()).andStubReturn(DatabaseObjectType.TABLE);
   	expect(mockTableInfo.getType()).andStubReturn("MATERIALIZED");
   	
	}

}
