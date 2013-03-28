
package net.sourceforge.squirrel_sql.client.session;

import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.expectLastCall;
import static org.easymock.EasyMock.isA;
import static org.easymock.classextension.EasyMock.createMock;
import static org.easymock.classextension.EasyMock.replay;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotSame;
import net.sourceforge.squirrel_sql.BaseSQuirreLJUnit4TestCase;
import net.sourceforge.squirrel_sql.client.AppTestUtil;
import net.sourceforge.squirrel_sql.client.IApplication;
import net.sourceforge.squirrel_sql.client.gui.db.SQLAlias;
import net.sourceforge.squirrel_sql.client.preferences.SquirrelPreferences;
import net.sourceforge.squirrel_sql.client.session.event.ISessionListener;
import net.sourceforge.squirrel_sql.client.session.properties.SessionProperties;
import net.sourceforge.squirrel_sql.fw.FwTestUtil;
import net.sourceforge.squirrel_sql.fw.id.IIdentifier;
import net.sourceforge.squirrel_sql.fw.sql.IQueryTokenizer;
import net.sourceforge.squirrel_sql.fw.sql.ISQLDriver;
import net.sourceforge.squirrel_sql.fw.sql.SQLConnection;
import net.sourceforge.squirrel_sql.fw.util.TaskThreadPool;

import org.easymock.classextension.EasyMock;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class SessionTest extends BaseSQuirreLJUnit4TestCase
{

	Session sessionUnderTest = null;

	
	IApplication mockApplication = createMock(IApplication.class);

	SessionManager mockSessionManager = createMock(SessionManager.class);

	ISQLDriver mockSqlDriver = createMock(ISQLDriver.class);

	SQLAlias mockSqlAlias = null;

	SQLConnection mockSqlConnection = null;

	SquirrelPreferences mockSquirrePrefs = null;

	IIdentifier mockIidentifier = createMock(IIdentifier.class);

	IIdentifier mockSqlAliasId = createMock(IIdentifier.class);

	IIdentifier mockSqlDriverId = createMock(IIdentifier.class);

	TaskThreadPool mockTaskThreadPool = createMock(TaskThreadPool.class);

	SessionProperties props = null;

	static final String FIRST_STMT_SEP = ";";

	static final String SECOND_STMT_SEP = "FOO";

	static final String CUSTOM_STMT_SEP = "CustomSeparator";

	@Before
	public void setUp() throws Exception
	{
		
		props = getEasyMockSessionProperties();

		mockSquirrePrefs = AppTestUtil.getEasyMockSquirrelPreferences(props);
		mockSqlAlias = AppTestUtil.getEasyMockSQLAlias(mockSqlAliasId, mockSqlDriverId);
		mockSqlConnection = FwTestUtil.getEasyMockSQLConnection();

		mockSessionManager.addSessionListener(isA(ISessionListener.class));
		mockSessionManager.addSessionListener(isA(ISessionListener.class));

		replay(mockSessionManager);

		expect(mockApplication.getSessionManager()).andReturn(mockSessionManager).anyTimes();
		expect(mockApplication.getSquirrelPreferences()).andReturn(mockSquirrePrefs).anyTimes();
		expect(mockApplication.getThreadPool()).andReturn(mockTaskThreadPool).anyTimes();

		replay(mockApplication);
		replay(mockSqlDriver);
		replay(mockSqlConnection);

		sessionUnderTest =
			new Session(mockApplication, mockSqlDriver, mockSqlAlias, mockSqlConnection, "user", "password",
				mockIidentifier);
	}

	@After
	public void tearDown() throws Exception
	{
		sessionUnderTest = null;
	}

	
	@Test(expected = IllegalArgumentException.class)
	public final void testSessionNullApp()
	{
		new Session(null, mockSqlDriver, mockSqlAlias, mockSqlConnection, "user", "password", mockIidentifier);
	}

	@Test(expected = IllegalArgumentException.class)
	public final void testSessionNullDriver()
	{
		new Session(mockApplication, null, mockSqlAlias, mockSqlConnection, "user", "password", mockIidentifier);
	}

	@Test(expected = IllegalArgumentException.class)
	public final void testSessionNullAlias()
	{
		new Session(mockApplication, mockSqlDriver, null, mockSqlConnection, "user", "password",
			mockIidentifier);
	}

	@Test(expected = IllegalArgumentException.class)
	public final void testSessionNullConnection()
	{
		new Session(mockApplication, mockSqlDriver, mockSqlAlias, null, "user", "password", mockIidentifier);
	}

	@Test(expected = IllegalArgumentException.class)
	public final void testSessionNullSessionId()
	{
		new Session(mockApplication, mockSqlDriver, mockSqlAlias, mockSqlConnection, "user", "password", null);
	}

	@Test(expected = IllegalArgumentException.class)
	public final void testSessionNullQueryTokenizer()
	{
		sessionUnderTest.setQueryTokenizer(null);
	}

	

	@Test
	public final void testGetQueryTokenizer_Default()
	{
		
		
		IQueryTokenizer qt1 = sessionUnderTest.getQueryTokenizer();

		
		

		assertEquals(FIRST_STMT_SEP, qt1.getSQLStatementSeparator());

		
		
	}

	@Test
	public final void testGetQueryTokenizer_Custom()
	{
		IQueryTokenizer customTokenizer = FwTestUtil.getEasyMockQueryTokenizer(CUSTOM_STMT_SEP, "--", true, 0);

		sessionUnderTest.setQueryTokenizer(customTokenizer);

		IQueryTokenizer retrievedTokenizer = sessionUnderTest.getQueryTokenizer();

		assertEquals(CUSTOM_STMT_SEP, retrievedTokenizer.getSQLStatementSeparator());
	}

	@Test
	public final void testGetQueryTokenizer_CustomAfterGet()
	{
		
		IQueryTokenizer initialTokenizer = sessionUnderTest.getQueryTokenizer();
		assertEquals(FIRST_STMT_SEP, initialTokenizer.getSQLStatementSeparator());

		IQueryTokenizer customTokenizer = FwTestUtil.getEasyMockQueryTokenizer(CUSTOM_STMT_SEP, "--", true, 0);

		
		sessionUnderTest.setQueryTokenizer(customTokenizer);

		IQueryTokenizer retrievedTokenizer = sessionUnderTest.getQueryTokenizer();

		assertEquals(CUSTOM_STMT_SEP, retrievedTokenizer.getSQLStatementSeparator());

		
		
		assertNotSame(initialTokenizer, retrievedTokenizer);
	}

	@Test(expected = IllegalStateException.class)
	public final void testSetQueryTokenizer()
	{
		IQueryTokenizer customTokenizer1 = FwTestUtil.getEasyMockQueryTokenizer(FIRST_STMT_SEP, "--", true, 0);

		IQueryTokenizer customTokenizer2 = FwTestUtil.getEasyMockQueryTokenizer(SECOND_STMT_SEP, "--", true, 0);

		sessionUnderTest.setQueryTokenizer(customTokenizer1);

		
		
		sessionUnderTest.setQueryTokenizer(customTokenizer2);
	}

   private SessionProperties getEasyMockSessionProperties() {
      
      SessionProperties result = EasyMock.createMock(SessionProperties.class);
      expect(result.getSQLStatementSeparator()).andReturn(";").once();
      expect(result.getSQLStatementSeparator()).andReturn("FOO").once();
      expect(result.getStartOfLineComment()).andReturn("--").anyTimes();
      expect(result.clone()).andReturn(result);
      expect(result.getRemoveMultiLineComment()).andReturn(true).anyTimes();
      result.setSQLStatementSeparator(isA(String.class));
      expectLastCall().anyTimes();
      result.setStartOfLineComment(isA(String.class));
      expectLastCall().anyTimes();
      result.setRemoveMultiLineComment(EasyMock.anyBoolean());
      expectLastCall().anyTimes();
      replay(result);
      return result;
  }
}
