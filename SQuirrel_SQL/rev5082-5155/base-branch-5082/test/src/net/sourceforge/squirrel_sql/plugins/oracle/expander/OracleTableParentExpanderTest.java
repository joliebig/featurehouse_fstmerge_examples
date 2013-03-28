
package net.sourceforge.squirrel_sql.plugins.oracle.expander;

import static org.easymock.EasyMock.aryEq;
import static org.easymock.EasyMock.eq;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.expectLastCall;
import static org.easymock.EasyMock.isA;
import net.sourceforge.squirrel_sql.client.session.mainpanel.objecttree.AbstractINodeExpanderTest;
import net.sourceforge.squirrel_sql.client.session.schemainfo.ObjFilterMatcher;
import net.sourceforge.squirrel_sql.fw.sql.ITableInfo;
import net.sourceforge.squirrel_sql.plugins.oracle.prefs.OraclePreferenceBean;

import org.junit.Before;

public class OracleTableParentExpanderTest extends AbstractINodeExpanderTest
{

	@Before
	public void setUp() throws Exception
	{
		OraclePreferenceBean prefs = mockHelper.createMock(OraclePreferenceBean.class);
		classUnderTest = new OracleTableParentExpander(prefs);

		expect(prefs.isExcludeRecycleBinTables()).andStubReturn(true);
		ITableInfo mockTableInfo = mockHelper.createMock(ITableInfo.class);
		expect(mockTableInfo.getSimpleName()).andReturn(TEST_SIMPLE_NAME);
		String[] tableTypes = new String[] { TEST_SIMPLE_NAME };
		ITableInfo[] tables = new ITableInfo[] { mockTableInfo };
		expect(
			mockSchemaInfo.getITableInfos(eq(TEST_CATALOG_NAME), eq(TEST_SCHEMA_NAME),
				isA(ObjFilterMatcher.class), aryEq(tableTypes))).andStubReturn(tables);
		mockSchemaInfo.waitTillTablesLoaded();
		expectLastCall().anyTimes();
		expect(mockSessionProperties.getShowRowCount()).andStubReturn(false);

	}

}
