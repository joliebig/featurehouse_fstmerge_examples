
package net.sourceforge.squirrel_sql.client.session.mainpanel.objecttree.tabs;

import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.isA;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.Statement;
import java.sql.Types;

import net.sourceforge.squirrel_sql.client.session.properties.SessionProperties;
import net.sourceforge.squirrel_sql.fw.datasetviewer.DataSetListModel;
import net.sourceforge.squirrel_sql.fw.sql.IDatabaseObjectInfo;

import org.junit.After;
import org.junit.Test;


public abstract class AbstractStatementTabTest extends AbstractTabTest
{

	protected BaseObjectTab classUnderTest = null;

	public static final String STMT_SEP = ";";

	public static final String HINT = "aHint";

	protected SessionProperties mockSessionProperties = mockHelper.createMock(SessionProperties.class);

	protected IDatabaseObjectInfo mockDatabaseObjectInfo = mockHelper.createMock(IDatabaseObjectInfo.class);

	protected Statement mockStatement = mockHelper.createMock(Statement.class);

	protected ResultSet mockResultSet = mockHelper.createMock(ResultSet.class);

	protected ResultSetMetaData mockResultSetMetaData = mockHelper.createMock(ResultSetMetaData.class);

	public AbstractStatementTabTest()
	{
		super();
	}

	@After
	public void tearDown() throws Exception
	{
		classUnderTest = null;
	}

	@Test
	public void testSelect() throws Exception
	{

		expect(mockSession.getApplication()).andStubReturn(mockApplication);
		expect(mockSession.getIdentifier()).andStubReturn(mockSessionId);
		expect(mockSession.getProperties()).andStubReturn(mockSessionProperties);
		expect(mockSession.getMetaData()).andStubReturn(mockSQLMetaData);
		expect(mockSessionProperties.getMetaDataOutputClassName()).andStubReturn(
			DataSetListModel.class.getName());
		expect(mockApplication.getSessionManager()).andStubReturn(mockSessionManager);
		expect(mockSessionManager.getSession(mockSessionId)).andStubReturn(mockSession);
		expect(mockDatabaseObjectInfo.getSchemaName()).andStubReturn(TEST_SCHEMA_NAME);
		expect(mockDatabaseObjectInfo.getCatalogName()).andStubReturn(TEST_CATALOG_NAME);
		expect(mockDatabaseObjectInfo.getSimpleName()).andStubReturn(TEST_SIMPLE_NAME);
		expect(mockDatabaseObjectInfo.getQualifiedName()).andStubReturn(TEST_QUALIFIED_NAME);
		expect(mockSession.getSQLConnection()).andStubReturn(mockSQLConnection);
		expect(mockSQLConnection.createStatement()).andStubReturn(mockStatement);
		expect(mockSQLMetaData.getDatabaseProductName()).andStubReturn(databaseProductName);
		expect(mockSQLMetaData.getDatabaseProductVersion()).andStubReturn(DATABASE_PRODUCT_VERSION);
		expect(mockStatement.executeQuery(isA(String.class))).andStubReturn(mockResultSet);
		expect(mockResultSet.next()).andStubReturn(false);
		expect(mockResultSet.getMetaData()).andStubReturn(mockResultSetMetaData);
		expect(mockResultSetMetaData.getColumnCount()).andStubReturn(1);
		expect(mockResultSetMetaData.isNullable(1)).andStubReturn(ResultSetMetaData.columnNoNulls);
		expect(mockResultSetMetaData.getPrecision(1)).andStubReturn(10);
		expect(mockResultSetMetaData.isSigned(1)).andReturn(true);
		expect(mockResultSetMetaData.isCurrency(1)).andReturn(true);
		expect(mockResultSetMetaData.isAutoIncrement(1)).andReturn(true);
		expect(mockResultSetMetaData.getColumnName(1)).andReturn(TEST_COLUMN_NAME);
		expect(mockResultSetMetaData.getColumnTypeName(1)).andReturn("VARCHAR");
		expect(mockResultSetMetaData.getColumnType(1)).andReturn(Types.VARCHAR);
		expect(mockResultSetMetaData.getColumnDisplaySize(1)).andStubReturn(10);
		expect(mockResultSetMetaData.getColumnLabel(1)).andStubReturn(TEST_COLUMN_NAME);
		expect(mockResultSetMetaData.getScale(1)).andStubReturn(3);
		mockResultSet.close();
		mockStatement.close();

		mockHelper.replayAll();
		classUnderTest.setSession(mockSession);
		classUnderTest.setDatabaseObjectInfo(mockDatabaseObjectInfo);
		classUnderTest.getComponent();
		classUnderTest.select();
		mockHelper.verifyAll();
	}

}