
package net.sourceforge.squirrel_sql.client.session.mainpanel.objecttree.tabs.table;


import net.sourceforge.squirrel_sql.client.session.mainpanel.objecttree.tabs.AbstractBaseDataSetTabTest;
import net.sourceforge.squirrel_sql.fw.sql.ITableInfo;

import org.easymock.EasyMock;
import org.junit.Before;

public class IndexesTabTest extends AbstractBaseDataSetTabTest
{

	@Before
	public void setUp() throws Exception
	{
		super.setUp();
		classUnderTest = new IndexesTab();
		super.clazz = IndexesTab.class;
		
		ITableInfo localMockTableInfo = mockHelper.createMock("localMockTableInfo", ITableInfo.class);
		EasyMock.expect(localMockTableInfo.getType()).andStubReturn("testTableType");
		
		((IndexesTab)classUnderTest).setTableInfo(localMockTableInfo);
		
	}


}
