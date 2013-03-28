
package net.sourceforge.squirrel_sql.client.session.mainpanel.objecttree.tabs.table;

import static org.easymock.EasyMock.aryEq;
import static org.easymock.EasyMock.eq;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.isNull;
import net.sourceforge.squirrel_sql.client.session.mainpanel.objecttree.tabs.AbstractBaseDataSetTabTest;
import net.sourceforge.squirrel_sql.fw.sql.ITableInfo;

import org.junit.Before;

public class PrimaryKeyTabTest extends AbstractBaseDataSetTabTest
{

	private int[] columnIndices = new int[] { 6, 5, 4 };

	@Before
	public void setUp() throws Exception
	{
		super.setUp();
		classUnderTest = new PrimaryKeyTab();
		super.clazz = PrimaryKeyTab.class;

		expect(mockSQLMetaData.getPrimaryKey((ITableInfo) isNull(), aryEq(columnIndices), eq(true))).andStubReturn(
			null);
	}

}
