
package net.sourceforge.squirrel_sql.client.session.mainpanel.objecttree.tabs.table;

import static org.easymock.EasyMock.expect;
import static org.junit.Assert.assertNotNull;
import net.sourceforge.squirrel_sql.BaseSQuirreLJUnit4TestCase;
import net.sourceforge.squirrel_sql.client.IApplication;
import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.client.session.SessionManager;
import net.sourceforge.squirrel_sql.fw.datasetviewer.IDataSet;
import net.sourceforge.squirrel_sql.fw.id.IIdentifier;
import net.sourceforge.squirrel_sql.fw.sql.ISQLConnection;
import net.sourceforge.squirrel_sql.fw.sql.ITableInfo;
import net.sourceforge.squirrel_sql.fw.sql.SQLDatabaseMetaData;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class AbstractForeignKeysTabTest extends BaseSQuirreLJUnit4TestCase
{

	protected ForeignKeysBaseTab classUnderTest = null;
	ITableInfo mockTableInfo = mockHelper.createMock("mockTableInfo", ITableInfo.class);
	protected ISession mockSession = mockHelper.createMock("mockSession", ISession.class);
	protected IApplication mockApplication = mockHelper.createMock("mockApplication", IApplication.class);
	protected IIdentifier mockIdentifier = mockHelper.createMock("mockIdentifier", IIdentifier.class);
	protected SessionManager mockSessionManager = mockHelper.createMock("mockSessionManager", SessionManager.class);
	protected ISQLConnection mockSQLConnection = mockHelper.createMock("mockSQLConnection", ISQLConnection.class);
	protected SQLDatabaseMetaData mockSQLDatabaseMetaData = mockHelper.createMock("mockSQLDatabaseMetaData", SQLDatabaseMetaData.class);

	public AbstractForeignKeysTabTest()
	{
		super();
	}

	@Before
	public void setUp() throws Exception
	{
		expect(mockSession.getApplication()).andStubReturn(mockApplication);
		expect(mockSession.getIdentifier()).andStubReturn(mockIdentifier);
		expect(mockSession.getSQLConnection()).andStubReturn(mockSQLConnection);
		
		expect(mockApplication.getSessionManager()).andStubReturn(mockSessionManager);
		expect(mockSessionManager.getSession(mockIdentifier)).andStubReturn(mockSession);
		
		expect(mockSQLConnection.getSQLMetaData()).andStubReturn(mockSQLDatabaseMetaData);
	}
	
	@After
	public void tearDown() throws Exception
	{
		classUnderTest = null;
	}

	@Test
	public void testGetHintKey()
	{
		mockHelper.replayAll();
		assertNotNull(classUnderTest.getHintKey());
		mockHelper.verifyAll();
	}

	@Test
	public void testGetHint()
	{
		mockHelper.replayAll();
		assertNotNull(classUnderTest.getHint());
		mockHelper.verifyAll();
	}

	@Test
	public void testGetTitleKey()
	{
		mockHelper.replayAll();
		assertNotNull(classUnderTest.getTitleKey());
		mockHelper.verifyAll();
	}

	@Test
	public void testGetTitle()
	{
		mockHelper.replayAll();
		assertNotNull(classUnderTest.getTitle());
		mockHelper.verifyAll();
	}

	@Test
	public void testCreateDataSet() throws Exception
	{
		
		IDataSet mockImportedKeysDataSet = mockHelper.createMock("mockImportedKeysDataSet", IDataSet.class);
		IDataSet mockExportedKeysDataSet = mockHelper.createMock("mockExportedKeysDataSet", IDataSet.class);
		
		expect(mockSQLDatabaseMetaData.getImportedKeysDataSet(mockTableInfo)).andStubReturn(mockImportedKeysDataSet);
		expect(mockSQLDatabaseMetaData.getExportedKeysDataSet(mockTableInfo)).andStubReturn(mockExportedKeysDataSet);
		
		mockHelper.replayAll();
		classUnderTest.setTableInfo(mockTableInfo);
		classUnderTest.setSession(mockSession);
		classUnderTest.setDatabaseObjectInfo(mockTableInfo);
		IDataSet result = classUnderTest.createDataSet();
		assertNotNull(result);
		mockHelper.verifyAll();
	}

}