
package net.sourceforge.squirrel_sql.client.session.mainpanel.objecttree.tabs.table;

import net.sourceforge.squirrel_sql.client.session.mainpanel.objecttree.tabs.AbstractBaseDataSetTabTest;
import net.sourceforge.squirrel_sql.fw.sql.ITableInfo;

import org.easymock.EasyMock;
import org.junit.Before;

public class ColumnsTabTest extends AbstractBaseDataSetTabTest
{
	private static int[] columnIndices = new int[] { 4, 6, 18, 9, 7, 13, 12, 5, 8, 10, 11, 14, 15, 16, 17 };

	@Before
	public void setUp() throws Exception
	{
		super.setUp();
		classUnderTest = new ColumnsTab();
		super.clazz = ColumnsTab.class;
		EasyMock.expect(mockSQLMetaData.getColumns((ITableInfo) EasyMock.isNull(),
			EasyMock.aryEq(columnIndices), EasyMock.eq(true))).andStubReturn(null);
	}

}
