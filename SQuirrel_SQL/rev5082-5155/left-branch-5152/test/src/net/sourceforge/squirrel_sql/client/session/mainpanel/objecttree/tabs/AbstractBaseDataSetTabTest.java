
package net.sourceforge.squirrel_sql.client.session.mainpanel.objecttree.tabs;

import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.expectLastCall;
import static org.easymock.EasyMock.isA;

import java.lang.reflect.Method;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.util.Date;

import net.sourceforge.squirrel_sql.client.session.schemainfo.SchemaInfo;
import net.sourceforge.squirrel_sql.fw.datasetviewer.IDataSet;
import net.sourceforge.squirrel_sql.fw.sql.IDatabaseObjectInfo;
import net.sourceforge.squirrel_sql.fw.sql.ISQLDriver;

import org.junit.After;
import org.junit.Assert;
import org.junit.Test;

public class AbstractBaseDataSetTabTest extends AbstractTabTest
{
	
	protected BaseDataSetTab classUnderTest = null;

	
	protected Class<? extends BaseDataSetTab> clazz = null;

	

	protected IDataSet mockDataSet = mockHelper.createMock(IDataSet.class);

	protected DatabaseMetaData mockDatabaseMetaData = mockHelper.createMock(DatabaseMetaData.class);

	protected ISQLDriver mockSQLDriver = mockHelper.createMock(ISQLDriver.class);

	protected SchemaInfo mockSchemaInfo = mockHelper.createMock(SchemaInfo.class);

	protected IDatabaseObjectInfo mockDatabaseObjectInfo = mockHelper.createMock(IDatabaseObjectInfo.class);
	
	

	public static final String[] SQL_KEYWORDS = new String[] { "testKeyword1", "testKeyword2" };

	public static final String[] SCHEMAS = new String[] { "testSchema1", "testSchema2" };

	public static final String[] NUMERIC_FUNCTIONS =
		new String[] { "testNumericFunction1", "testNumericFunction2" };

	public static final String[] DRIVER_JAR_FILE_NAMES = new String[] { "jarFilename1", "jarFilename2" };

	public static final String TEST_DRIVER_CLASS_NAME = "aTestDriverClassName";

	public static final String[] STRING_FUNCTIONS = new String[] { "stringFunction1", "stringFunction2" };

	public static final String[] TABLE_TYPES = new String[] { "TABLE" };

	public static final String[] SYSTEM_FUNCTIONS = new String[] {"systemFunction1", "systemFunction2"};
	
	public static final String[] TIME_DATE_FUNCTIONS = new String[] {"currentTime", "currentDate"};
	
	public AbstractBaseDataSetTabTest()
	{
		super();
	}

	public void setUp() throws Exception
	{
		
		expect(mockSession.getApplication()).andStubReturn(mockApplication);
		expect(mockSession.getSQLConnection()).andStubReturn(mockSQLConnection);
		expect(mockSession.getMetaData()).andStubReturn(mockSQLMetaData);
		expect(mockSession.getIdentifier()).andStubReturn(mockSessionId);
		expect(mockSession.getDriver()).andStubReturn(mockSQLDriver);
		expect(mockSession.getSchemaInfo()).andStubReturn(mockSchemaInfo);

		
		expect(mockApplication.getSessionManager()).andStubReturn(mockSessionManager);
		expect(mockApplication.getThreadPool()).andStubReturn(mockThreadPool);

		
		expect(mockSessionManager.getSession(mockSessionId)).andStubReturn(mockSession);

		
		expect(mockSQLConnection.getSQLMetaData()).andStubReturn(mockSQLMetaData);
		expect(mockSQLConnection.getConnection()).andStubReturn(mockConnection);
		expect(mockSQLConnection.getCatalog()).andStubReturn(TEST_CATALOG_NAME);
		expect(mockSQLConnection.getAutoCommit()).andStubReturn(true);
		expect(mockSQLConnection.getTimeOpened()).andStubReturn(new Date());

		
		expect(mockSQLMetaData.getTypesDataSet()).andStubReturn(mockDataSet);
		expect(mockSQLMetaData.getSQLKeywords()).andStubReturn(SQL_KEYWORDS);
		expect(mockSQLMetaData.getJDBCMetaData()).andStubReturn(mockDatabaseMetaData);
		expect(mockSQLMetaData.getCatalogs()).andStubReturn(mockCatalogs);
		expect(mockSQLMetaData.getNumericFunctions()).andStubReturn(NUMERIC_FUNCTIONS);
		expect(mockSQLMetaData.getStringFunctions()).andStubReturn(STRING_FUNCTIONS);
		expect(mockSQLMetaData.getSystemFunctions()).andStubReturn(SYSTEM_FUNCTIONS);
		expect(mockSQLMetaData.getTableTypes()).andStubReturn(TABLE_TYPES);
		expect(mockSQLMetaData.getTimeDateFunctions()).andStubReturn(TIME_DATE_FUNCTIONS);
		
		
		expect(mockSchemaInfo.getSchemas()).andStubReturn(SCHEMAS);

		
		mockThreadPool.addTask(isA(Runnable.class));
		expectLastCall().anyTimes();

		
		expect(mockConnection.isClosed()).andStubReturn(false);
		expect(mockConnection.isReadOnly()).andStubReturn(false);
		expect(mockConnection.getCatalog()).andStubReturn(TEST_CATALOG_NAME);
		expect(mockConnection.getTransactionIsolation()).andStubReturn(TRANSACTION_ISOLATION);

		
		expect(mockSQLDriver.getDriverClassName()).andStubReturn(TEST_DRIVER_CLASS_NAME);
		expect(mockSQLDriver.getJarFileNames()).andStubReturn(DRIVER_JAR_FILE_NAMES);

		
		expect(mockDatabaseMetaData.getDefaultTransactionIsolation()).andStubReturn(
			Connection.TRANSACTION_READ_COMMITTED);
		
		
		expect(mockDatabaseObjectInfo.getCatalogName()).andStubReturn(TEST_CATALOG_NAME);
		expect(mockDatabaseObjectInfo.getSchemaName()).andStubReturn(TEST_SCHEMA_NAME);
		expect(mockDatabaseObjectInfo.getSimpleName()).andStubReturn(TEST_SIMPLE_NAME);
		expect(mockDatabaseObjectInfo.getQualifiedName()).andStubReturn(TEST_QUALIFIED_NAME);

	}

	@After
	public void tearDown() throws Exception
	{
		classUnderTest = null;
	}

	@Test
	public void testRefreshComponent() throws Exception
	{
		mockHelper.replayAll();
		classUnderTest.setSession(mockSession);
		classUnderTest.refreshComponent();
		mockHelper.verifyAll();
	}

	@Test
	public void testGetHint()
	{
		mockHelper.replayAll();
		classUnderTest.setSession(mockSession);
		Assert.assertNotNull(classUnderTest.getHint());
		mockHelper.verifyAll();
	}

	@Test
	public void testGetTitle()
	{
		mockHelper.replayAll();
		classUnderTest.setSession(mockSession);
		Assert.assertNotNull(classUnderTest.getTitle());
		mockHelper.verifyAll();
	}

	@Test
	public void testCreateDataSet() throws Exception
	{
		mockHelper.replayAll();
		classUnderTest.setSession(mockSession);
		if (clazz != null)
		{
			Method m = clazz.getDeclaredMethod("createDataSet", (Class[]) null);
			m.setAccessible(true);
			m.invoke(classUnderTest, (Object[]) null);
		}
		mockHelper.verifyAll();
	}

}