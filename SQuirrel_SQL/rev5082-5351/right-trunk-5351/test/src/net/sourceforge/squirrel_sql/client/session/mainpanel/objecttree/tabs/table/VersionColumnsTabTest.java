
package net.sourceforge.squirrel_sql.client.session.mainpanel.objecttree.tabs.table;


import static org.easymock.EasyMock.isNull;
import net.sourceforge.squirrel_sql.client.session.mainpanel.objecttree.tabs.AbstractBaseDataSetTabTest;
import net.sourceforge.squirrel_sql.fw.sql.ITableInfo;

import org.easymock.EasyMock;
import org.junit.Before;

public class VersionColumnsTabTest extends AbstractBaseDataSetTabTest
{

	@Before
	public void setUp() throws Exception
	{
		super.setUp();
		classUnderTest = new VersionColumnsTab();
		super.clazz = VersionColumnsTab.class;		
		
		EasyMock.expect(
			mockSQLMetaData.getVersionColumnsDataSet((ITableInfo) isNull())).andStubReturn(null);
		
	}


}
