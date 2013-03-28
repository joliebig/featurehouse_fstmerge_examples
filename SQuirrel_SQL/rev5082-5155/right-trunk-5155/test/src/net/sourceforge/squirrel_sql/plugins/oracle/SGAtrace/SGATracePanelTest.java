
package net.sourceforge.squirrel_sql.plugins.oracle.SGAtrace;


import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.isA;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import net.sourceforge.squirrel_sql.BaseSQuirreLJUnit4TestCase;
import net.sourceforge.squirrel_sql.client.IApplication;
import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.fw.sql.ISQLConnection;

import org.easymock.EasyMock;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;


public class SGATracePanelTest extends BaseSQuirreLJUnit4TestCase
{

	SGATracePanel classUnderTest = null;
	private ISession mockSession = mockHelper.createMock(ISession.class);
	private ISQLConnection mockSQLConnection = mockHelper.createMock(ISQLConnection.class);
	private Connection mockConnection = mockHelper.createMock(Connection.class);
	private IApplication mockApplication = mockHelper.createMock(IApplication.class);
	
	private PreparedStatement mockPreparedStatementForAccessible = 
		mockHelper.createMock("mockPreparedStatementForAccessible", PreparedStatement.class);
	private ResultSet mockResultSetForAccessible = mockHelper.createMock(ResultSet.class);
	
	private PreparedStatement mockPreparedStatementForSGATrace = 	
		mockHelper.createMock("mockPreparedStatementForSGATrace", PreparedStatement.class);
	private ResultSet mockResultSetForSGATrace = mockHelper.createMock(ResultSet.class);
	
	@Before
	public void setUp() throws Exception
	{
		
	}

	@After
	public void tearDown() throws Exception
	{
	}
	
	@Test
	public void testPopulateSGATrace() throws SQLException {
		
		expect(mockSession.getSQLConnection()).andStubReturn(mockSQLConnection);
		expect(mockSession.getApplication()).andStubReturn(mockApplication);
		
		
		expect(mockSQLConnection.prepareStatement(isA(String.class))).andReturn(mockPreparedStatementForAccessible);
		expect(mockPreparedStatementForAccessible.executeQuery()).andStubReturn(mockResultSetForAccessible);
		
		mockResultSetForAccessible.close();
		expect(mockResultSetForAccessible.getStatement()).andStubReturn(mockPreparedStatementForAccessible);
		mockPreparedStatementForAccessible.close();
		
		
		expect(mockSQLConnection.getConnection()).andStubReturn(mockConnection);
		expect(mockConnection.prepareStatement(isA(String.class))).andReturn(mockPreparedStatementForSGATrace);
		expect(mockPreparedStatementForSGATrace.execute()).andStubReturn(true);
		expect(mockPreparedStatementForSGATrace.getResultSet()).andStubReturn(mockResultSetForSGATrace);
		expect(mockResultSetForSGATrace.next()).andReturn(true).andReturn(false);
		expect(mockResultSetForSGATrace.getString(EasyMock.anyInt())).andStubReturn("testValue");
		
		mockResultSetForSGATrace.close();
		expect(mockResultSetForSGATrace.getStatement()).andStubReturn(mockPreparedStatementForSGATrace);
		mockPreparedStatementForSGATrace.close();
		
		
		mockHelper.replayAll();
		classUnderTest = new SGATracePanel(mockSession, 0);
		mockHelper.verifyAll();
	}

}
