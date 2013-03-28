
package net.sourceforge.squirrel_sql.client.session.mainpanel.objecttree.tabs.table;


import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.isNull;
import net.sourceforge.squirrel_sql.client.session.mainpanel.objecttree.tabs.AbstractBaseDataSetTabTest;
import net.sourceforge.squirrel_sql.fw.sql.ITableInfo;
import net.sourceforge.squirrel_sql.fw.sql.dbobj.BestRowIdentifier;

import org.junit.Before;

public class RowIDTabTest extends AbstractBaseDataSetTabTest
{

	@Before
	public void setUp() throws Exception
	{
		super.setUp();
		classUnderTest = new RowIDTab();
		super.clazz = RowIDTab.class;		
		
		expect(mockSQLMetaData.getBestRowIdentifier((ITableInfo) isNull())).andStubReturn(new BestRowIdentifier[] {});
	}

}
