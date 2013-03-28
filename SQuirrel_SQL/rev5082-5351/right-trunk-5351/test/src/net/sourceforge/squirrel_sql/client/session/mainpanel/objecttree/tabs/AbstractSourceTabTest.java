
package net.sourceforge.squirrel_sql.client.session.mainpanel.objecttree.tabs;

import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.expectLastCall;
import static org.easymock.EasyMock.isA;

import java.sql.PreparedStatement;
import java.sql.ResultSet;

import net.sourceforge.squirrel_sql.fw.sql.IDatabaseObjectInfo;
import net.sourceforge.squirrel_sql.fw.sql.ISQLDatabaseMetaData;

import org.easymock.EasyMock;
import org.junit.After;
import org.junit.Test;

public abstract class AbstractSourceTabTest extends AbstractTabTest
{


	protected BaseSourceTab classUnderTest = null;

	protected IDatabaseObjectInfo mockDatabaseObjectInfo = mockHelper.createMock(IDatabaseObjectInfo.class);

	protected PreparedStatement mockPreparedStatement = mockHelper.createMock(PreparedStatement.class);

	protected ResultSet mockResultSet = mockHelper.createMock(ResultSet.class);

	protected ISQLDatabaseMetaData mockMetaData = mockHelper.createMock(ISQLDatabaseMetaData.class);
	
	public AbstractSourceTabTest()
	{
		super();
	}

	@After
	public void tearDown() throws Exception
	{
		classUnderTest = null;
	}

	
	@Test
	public final void testSelect() throws Exception
	{

		expect(mockSession.getApplication()).andStubReturn(mockApplication);
		expect(mockSession.getIdentifier()).andStubReturn(mockSessionId);
		expect(mockSession.getMetaData()).andStubReturn(mockMetaData);
		expect(mockSession.getSQLConnection()).andStubReturn(mockSQLConnection);
		
		setupMockDatabaseObjectInfo();
		
		expect(mockApplication.getSessionManager()).andStubReturn(mockSessionManager);
		expect(mockSessionManager.getSession(mockSessionId)).andStubReturn(mockSession);
		
		expect(mockSQLConnection.prepareStatement(isA(String.class))).andStubReturn(mockPreparedStatement);
		mockPreparedStatement.setString(EasyMock.anyInt(), isA(String.class));
		expectLastCall().anyTimes();
		expect(mockPreparedStatement.executeQuery()).andReturn(mockResultSet);
		expect(mockResultSet.next()).andStubReturn(false);
		mockResultSet.close();
		mockPreparedStatement.close();

		mockHelper.replayAll();
		classUnderTest.getComponent();
		classUnderTest.setSession(mockSession);
		classUnderTest.setDatabaseObjectInfo(mockDatabaseObjectInfo);
		classUnderTest.select();
		mockHelper.verifyAll();
	}

	protected void setupMockDatabaseObjectInfo() {
		expect(mockDatabaseObjectInfo.getSchemaName()).andStubReturn(TEST_SCHEMA_NAME);
		expect(mockDatabaseObjectInfo.getCatalogName()).andStubReturn(TEST_CATALOG_NAME);
		expect(mockDatabaseObjectInfo.getSimpleName()).andStubReturn(TEST_SIMPLE_NAME);
		expect(mockDatabaseObjectInfo.getQualifiedName()).andStubReturn(TEST_QUALIFIED_NAME);		
	}
}