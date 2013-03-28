
package net.sourceforge.squirrel_sql.client.session.mainpanel.objecttree.tabs.table;

import static org.easymock.EasyMock.aryEq;
import static org.easymock.EasyMock.eq;
import static org.easymock.EasyMock.isNull;
import net.sourceforge.squirrel_sql.client.session.mainpanel.objecttree.tabs.AbstractBaseDataSetTabTest;
import net.sourceforge.squirrel_sql.fw.sql.ITableInfo;

import org.easymock.EasyMock;
import org.junit.Before;

public class ColumnPriviligesTabTest extends AbstractBaseDataSetTabTest
{
	private int[] columnIndices = new int[] { 4, 6, 7, 5, 8 };

	@Before
	public void setUp() throws Exception
	{
		super.setUp();
		classUnderTest = new ColumnPriviligesTab();
		super.clazz = ColumnPriviligesTab.class;
		EasyMock.expect(
			mockSQLMetaData.getColumnPrivilegesDataSet((ITableInfo) isNull(), aryEq(columnIndices), eq(true)))
			.andStubReturn(null);
	}

}
