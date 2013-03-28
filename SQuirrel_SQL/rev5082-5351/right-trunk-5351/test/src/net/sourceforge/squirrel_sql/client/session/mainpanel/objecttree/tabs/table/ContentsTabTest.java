
package net.sourceforge.squirrel_sql.client.session.mainpanel.objecttree.tabs.table;

import static java.sql.Types.VARCHAR;
import static org.easymock.EasyMock.expect;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.Statement;

import net.sourceforge.squirrel_sql.client.IApplication;
import net.sourceforge.squirrel_sql.client.preferences.SquirrelPreferences;
import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.client.session.mainpanel.objecttree.ObjectTreePanel;
import net.sourceforge.squirrel_sql.client.session.mainpanel.objecttree.tabs.AbstractBaseDataSetTabTest;
import net.sourceforge.squirrel_sql.fw.sql.ITableInfo;
import net.sourceforge.squirrel_sql.fw.sql.TableColumnInfo;
import net.sourceforge.squirrel_sql.fw.sql.dbobj.BestRowIdentifier;

import org.easymock.EasyMock;
import org.junit.Before;

import utils.EasyMockHelper;

public class ContentsTabTest extends AbstractBaseDataSetTabTest
{

	
	private EasyMockHelper localMockHelper = new EasyMockHelper();

	@Before
	public void setUp() throws Exception
	{
		super.setUp();
		ObjectTreePanel localMockObjectTreePanel =
			localMockHelper.createMock("localMockObjectTreePanel", ObjectTreePanel.class);
		SquirrelPreferences localMockSquirrelPreferences =
			localMockHelper.createMock("mockSquirrelPreferences", SquirrelPreferences.class);
		ISession localMockSession = localMockHelper.createMock("localMockSession", ISession.class);
		IApplication localMockApplication =
			localMockHelper.createMock("localMockApplication", IApplication.class);
		Statement localMockStatement = localMockHelper.createMock("localMockStatement", Statement.class);
		ITableInfo localMockTableInfo = localMockHelper.createMock("localMockTableInfo", ITableInfo.class);
		ResultSet localMockResultSet = localMockHelper.createMock("localMockResultSet", ResultSet.class);
		ResultSetMetaData localMockResultSetMetaData =
			localMockHelper.createMock("localMockResultSetMetaData", ResultSetMetaData.class);

		expect(localMockObjectTreePanel.getSession()).andStubReturn(localMockSession);
		expect(localMockSession.getApplication()).andStubReturn(localMockApplication);
		expect(localMockApplication.getSquirrelPreferences()).andStubReturn(localMockSquirrelPreferences);
		expect(localMockSquirrelPreferences.getShowPleaseWaitDialog()).andStubReturn(false);
		expect(localMockTableInfo.getQualifiedName()).andStubReturn("Test Qualified TableName");

		localMockStatement.close();
		expect(localMockResultSet.getMetaData()).andStubReturn(localMockResultSetMetaData);
		
		
		expect(localMockResultSetMetaData.getColumnCount()).andStubReturn(1);
		expect(localMockResultSetMetaData.isNullable(1)).andStubReturn(ResultSetMetaData.columnNullable);
		expect(localMockResultSetMetaData.getPrecision(1)).andStubReturn(1);
		expect(localMockResultSetMetaData.isSigned(1)).andStubReturn(false);
		expect(localMockResultSetMetaData.isCurrency(1)).andStubReturn(false);
		expect(localMockResultSetMetaData.isAutoIncrement(1)).andStubReturn(false);
		expect(localMockResultSetMetaData.getColumnName(1)).andStubReturn("testColumnName");
		expect(localMockResultSetMetaData.getColumnTypeName(1)).andStubReturn("VARCHAR");
		expect(localMockResultSetMetaData.getColumnType(1)).andStubReturn(VARCHAR);
		expect(localMockResultSetMetaData.getColumnDisplaySize(1)).andStubReturn(10);
		expect(localMockResultSetMetaData.getColumnLabel(1)).andStubReturn("testColumnLabel");
		expect(localMockResultSetMetaData.getScale(1)).andStubReturn(0);
		expect(localMockResultSet.next()).andReturn(true);
		expect(localMockResultSet.next()).andReturn(false);
		expect(localMockResultSet.getString(1)).andStubReturn("varcharval");
		expect(localMockResultSet.wasNull()).andReturn(false);
		localMockResultSet.close();
		
		expect(mockSQLConnection.createStatement()).andStubReturn(localMockStatement);
		expect(mockSQLMetaData.getColumnInfo(localMockTableInfo)).andStubReturn(new TableColumnInfo[] {});
		expect(mockSQLMetaData.getBestRowIdentifier(localMockTableInfo)).andStubReturn(new BestRowIdentifier[] {});
		expect(localMockStatement.executeQuery(EasyMock.isA(String.class))).andStubReturn(localMockResultSet);

		
		localMockHelper.replayAll();

		classUnderTest = new ContentsTab(localMockObjectTreePanel);
		classUnderTest.setDatabaseObjectInfo(localMockTableInfo);
		super.clazz = ContentsTab.class;
	}
	
}
